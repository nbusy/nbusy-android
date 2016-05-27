package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Optional;
import com.nbusy.app.InstanceProvider;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMap;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.Profile;
import com.nbusy.app.services.WorkerService;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.Client;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private final Context appContext;
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private final Profile userProfile;

    public Worker(final Context appContext, final Client client, final EventBus eventBus, DB db) {
        if (appContext == null) {
            throw new IllegalArgumentException("appContext cannot be null");
        }
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null");
        }
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null ");
        }

        this.appContext = appContext;
        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
    }

    // todo: should these be done by event bus + conn man ?

    /**
     * Event bus reg/unreg.
     */
    public void register(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        // a view is attaching to event bus so we need to ensure connectivity
        if (!client.isConnected() && userProfile != null) {
            client.connect(InstanceProvider.getConnManager());
        }

        // start the worker service if not running
        if (!WorkerService.RUNNING.get()) {
            Intent serviceIntent = new Intent(appContext, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, o.getClass().getSimpleName());
            appContext.startService(serviceIntent);
        }

        eventBus.register(o);
    }

    public void unregister(Object o) {
        eventBus.unregister(o);
        // todo: start 3 min disconnect standBy timer here in case a view wants to register again or we're in a brief limbo state
        // and update needConnection accordingly
    }

    /**
     * Whether worker needs an active connection to server.
     */
    public boolean needConnection() {
        // todo: or there are ongoing operations or queued operations or standby timer is still running
        return eventBus.haveSubscribers();
    }

    /************************
     * Server Communication *
     ************************/

    private void receiveMessages(MsgMessage... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMap.getNBusyMessages(msgs);
        final Set<Chat> chats = userProfile.upsertMessages(nbusyMsgs);
        db.upsertMessages(new DB.UpsertMessagesCallback() {
            @Override
            public void messagesUpserted() {
                for (Chat chat : chats) {
                    eventBus.post(new ChatsUpdatedEvent(chat));
                }
            }
        }, nbusyMsgs);
    }

    public void sendMessages(String chatId, String... msgs) {
        Optional<Chat.ChatAndNewMessages> cmOpt = userProfile.addNewOutgoingMessages(chatId, msgs);
        if (!cmOpt.isPresent()) {
            return;
        }

        sendMessages(cmOpt.get().messages);
    }

    public void sendMessages(Set<Message> msgs) {
        sendMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public void sendMessages(final Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        // handle echo messages separately
        if (Objects.equals(msgs[0].chatId, "echo")) {
            final Message m = msgs[0];
            client.echo(m.body, new EchoCallback() {
                @Override
                public void echoResponse(String msg) {
                    eventBus.post(new ChatsUpdatedEvent(userProfile.setMessageStatuses(Message.Status.DELIVERED_TO_USER, m)));
                    receiveMessages(new MsgMessage(m.chatId, "echo", null, m.sent, msg));
                }
            });
            return;
        }

        // update in memory user profile with messages in case any of them are new, and notify all listener about this state change
        Set<Chat> chats = userProfile.upsertMessages(msgs);
        eventBus.post(new ChatsUpdatedEvent(chats));

        // persist messages in the database with Status = NEW
        db.upsertMessages(new DB.UpsertMessagesCallback() {
            @Override
            public void messagesUpserted() {
                client.sendMessages(new SendMsgsCallback() {
                    @Override
                    public void sentToServer() {
                        // update in memory representation of messages
                        final Set<Chat> chats = userProfile.setMessageStatuses(Message.Status.SENT_TO_SERVER, msgs);

                        // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                        db.upsertMessages(new DB.UpsertMessagesCallback() {
                            @Override
                            public void messagesUpserted() {
                                // finally, notify all listening views about the changes
                                eventBus.post(new ChatsUpdatedEvent(chats));
                            }
                        }, msgs);
                    }
                }, DataMap.getTitanMessages(msgs));
            }
        }, msgs);
    }

    public boolean googleAuth(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }

        return client.googleAuth(token, new GoogleAuthCallback() {
            @Override
            public void success(GoogleAuthResponse res) {
                Log.i(TAG, "Authenticated with NBusy server using Google auth.");
            }

            @Override
            public void fail(int code, String message) {
                Log.i(TAG, "Failed to authenticate with NBusy server using Google auth: " + code + " : " + message);
            }
        });
    }

    /***********************
     * Database Operations *
     ***********************/

    /**
     * Retrieve messages from database, update in memory representation, notify views about the new data.
     */
    public void getChatMessages(final String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }

        db.getChatMessages(chatId, new DB.GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(List<Message> msgs) {
                if (msgs.size() != 0) {
                    eventBus.post(new ChatsUpdatedEvent(userProfile.upsertMessages(msgs)));
                }
            }
        });
    }
}
