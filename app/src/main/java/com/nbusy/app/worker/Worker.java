package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Optional;
import com.nbusy.app.activities.LoginActivity;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMap;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.Profile;
import com.nbusy.app.services.WorkerService;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.callbacks.JWTAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private final Context appContext = null;
    public final AtomicReference<Profile> userProfile = new AtomicReference<>();

    private ConnCallbacks connCallbacks = new ConnCallbacks() {
        @Override
        public void messagesReceived(MsgMessage... msgs) {
            receiveMessages(msgs);
        }

        @Override
        public void connected(String reason) {
            Log.i(TAG, "Connected to NBusy server with reason: " + reason);
            if (userProfile.get() == null) {
                throw new NullPointerException("userProfile is cannot be null while calling connect(...)");
            }

            client.jwtAuth(userProfile.get().JWTToken, new JWTAuthCallback() {
                @Override
                public void success() {
                    Log.i(TAG, "Authenticated with NBusy server using JWT auth.");
                    db.getQueuedMessages(new DB.GetChatMessagesCallback() {
                        @Override
                        public void chatMessagesRetrieved(final List<Message> msgs) {
                            final Message[] msgsArray = msgs.toArray(new Message[msgs.size()]);
                            client.sendMessages(new SendMsgsCallback() {
                                @Override
                                public void sentToServer() {
                                    // update in memory representation of messages
                                    final Set<Chat> chats = userProfile.get().setMessageStatuses(Message.Status.SENT_TO_SERVER, msgsArray);

                                    // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                                    db.upsertMessages(new DB.UpsertMessagesCallback() {
                                        @Override
                                        public void messagesUpserted() {
                                            Log.i(TAG, "Sent queued messages to server: " + msgs.size());
                                            // finally, notify all listening views about the changes
                                            if (!chats.isEmpty()) {
                                                eventBus.post(new ChatsUpdatedEvent(chats));
                                            }
                                        }
                                    }, msgsArray);
                                }
                            }, DataMap.getTitanMessages(msgsArray));
                        }
                    });
                }

                @Override
                public void fail() {
                    Log.i(TAG, "Authentication failed with NBusy server using JWT auth.");
                }
            });
        }

        @Override
        public void disconnected(String reason) {
            Log.w(TAG, "Connection attempt OR connection to NBusy server was shut down with reason: " + reason);
        }
    };

    public Worker(final Client client, final EventBus eventBus, DB db) {
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null");
        }
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null ");
        }

        this.client = client;
        this.eventBus = eventBus;
        this.db = db;

        db.getProfile(new DB.GetProfileCallback() {
            @Override
            public void profileRetrieved(Profile prof) {
                userProfile.set(prof);
                client.connect(connCallbacks);
                eventBus.post(new UserProfileRetrievedEvent(prof));
            }

            @Override
            public void error() {
                // no profile stored so display login activity
                Intent intent = new Intent(appContext, LoginActivity.class);
                appContext.startActivity(intent); // todo: what happens when service starts before user logs in?
            }
        });

        Log.i(TAG, "initialized");
    }

    public Worker() {
        this(new ClientImpl(), new EventBus(), new InMemDB());
    }

    public void destroy() {
        Log.i(TAG, "destroyed");
        client.close();
    }

    /**
     * Event bus reg/unreg.
     */
    public void register(Object o, Context c) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (c == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        // a view is attaching to event bus so we need to ensure connectivity
        if (!client.isConnected() && userProfile.get() != null) {
            client.connect(connCallbacks);
        }

        // start the worker service if not running
        if (!WorkerService.RUNNING.get()) {
            Intent serviceIntent = new Intent(c, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, o.getClass().getSimpleName());
            c.startService(serviceIntent);
        }

        eventBus.register(o);
    }

    public void unregister(Object o) {
        eventBus.unregister(o);
        // todo: start 3 min standBy timer here in case a view wants to register again or we're in a brief limbo state
    }

    /**
     * Whether worker needs an active connection to server.
     */
    public boolean needConnection() {
        return eventBus.haveSubscribers(); // todo: or there are ongoing operations or queued operations or standby timer is still running
    }

    /************************
     * Server Communication *
     ************************/

    private void receiveMessages(MsgMessage... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMap.getNBusyMessages(msgs);
        final Set<Chat> chats = userProfile.get().upsertMessages(nbusyMsgs);
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
        Optional<Chat.ChatAndNewMessages> cmOpt = userProfile.get().addNewOutgoingMessages(chatId, msgs);
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
                    eventBus.post(new ChatsUpdatedEvent(userProfile.get().setMessageStatuses(Message.Status.DELIVERED_TO_USER, m)));
                    receiveMessages(new MsgMessage(m.chatId, "echo", null, m.sent, msg));
                }
            });
            return;
        }

        // update in memory user profile with messages in case any of them are new, and notify all listener about this state change
        Set<Chat> chats = userProfile.get().upsertMessages(msgs);
        eventBus.post(new ChatsUpdatedEvent(chats));

        // persist messages in the database with Status = NEW
        db.upsertMessages(new DB.UpsertMessagesCallback() {
            @Override
            public void messagesUpserted() {
                client.sendMessages(new SendMsgsCallback() {
                    @Override
                    public void sentToServer() {
                        // update in memory representation of messages
                        final Set<Chat> chats = userProfile.get().setMessageStatuses(Message.Status.SENT_TO_SERVER, msgs);

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
                    eventBus.post(new ChatsUpdatedEvent(userProfile.get().upsertMessages(msgs)));
                }
            }
        });
    }
}
