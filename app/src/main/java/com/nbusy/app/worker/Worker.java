package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMaps;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.Profile;
import com.nbusy.app.services.WorkerService;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgsCallback;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNDU2MTQ5MjY0LCJ1c2VyaWQiOiIxIn0.wuKJ8CuDkCZYLmhgO-UlZd6v8nxKGk_PtkBwjalyjwA";
    private final List<Object> subscribers = new CopyOnWriteArrayList<>();
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    public Profile userProfile;
    private ConnCallbacks connCallbacks = new ConnCallbacks() {
        @Override
        public void messagesReceived(titan.client.messages.Message... msgs) {
            receiveMessages(msgs);
        }

        @Override
        public void connected() {
            Log.i(TAG, "Connected to NBusy server.");
            client.jwtAuth(JWT_TOKEN, new JwtAuthCallback() {
                @Override
                public void success() {
                    Log.i(TAG, "Authenticated with NBusy server.");
                }

                @Override
                public void fail() {
                    Log.i(TAG, "Authentication failed with NBusy server.");
                }
            });
        }

        @Override
        public void disconnected(String reason) {
            Log.w(TAG, "Failed to connect to NBusy server.");
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

        Log.i(TAG, "Instance created.");
        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        client.connect(connCallbacks);
        db.getProfile(new DB.GetProfileCallback() {
            @Override
            public void profileRetrieved(Profile up) {
                userProfile = up;
                eventBus.post(new UserProfileRetrievedEvent(userProfile));
            }
        });
    }

    public Worker() {
        this(new ClientImpl(), new AsyncEventBus(TAG, new UiThreadExecutor()), new InMemDB());
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

        // a view is attaching to event bus so we need to ensure connectivity
        if (!client.isConnected()) {
            client.connect(connCallbacks);
        }

        // start the worker service if not running
        if (!WorkerService.RUNNING.get() && c != null) {
            Intent serviceIntent = new Intent(c, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, o.getClass().getSimpleName());
            c.startService(serviceIntent);
        }

        subscribers.add(o);
        eventBus.register(o);
    }

    public void unregister(Object o) {
        subscribers.remove(o);
        eventBus.unregister(o);
        // todo: start 3 min standBy timer here in case a view wants to register again or we're in a brief limbo state
    }

    /**
     * Whether worker needs an active connection to server.
     */
    public boolean needConnection() {
        return !subscribers.isEmpty(); // todo: or there are ongoing operations or queued operations or standby timer is still running
    }

    /************************
     * Server Communication *
     ************************/

    private void receiveMessages(titan.client.messages.Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMaps.getNBusyMessages(msgs);
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
        sendMessages(userProfile.addNewOutgoingMessages(chatId, msgs).messages);
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
                    receiveMessages(new titan.client.messages.Message(m.chatId, "echo", null, m.sent, msg));
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
                }, DataMaps.getTitanMessages(msgs));
            }
        }, msgs);
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

    /*****************
     * Event Objects *
     *****************/

    public class UserProfileRetrievedEvent {
        public final Profile profile;

        public UserProfileRetrievedEvent(Profile profile) {
            if (profile == null) {
                throw new IllegalArgumentException("profile cannot be null");
            }
            this.profile = profile;
        }
    }

    public class ChatsUpdatedEvent {
        public final Set<Chat> chats;

        public ChatsUpdatedEvent(Chat... chats) {
            this(ImmutableSet.copyOf(chats));
        }

        public ChatsUpdatedEvent(Set<Chat> chats) {
            if (chats == null || chats.isEmpty()) {
                throw new IllegalArgumentException("chats cannot be null or empty");
            }
            this.chats = chats;
        }
    }
}
