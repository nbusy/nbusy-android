package com.nbusy.app.worker;

import android.util.Log;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMaps;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.Profile;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    public Profile userProfile;

    public Worker(final Client client, final EventBus eventBus, DB db) {
        Log.i(TAG, "Instance created.");
        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        client.connect(new ConnCallbacks() {
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
        });
        db.getProfile(new DB.GetProfileCallback() {
            @Override
            public void profileRetrieved(Profile up) {
                userProfile = up;
                eventBus.post(new UserProfileRetrievedEvent());
            }
        });
    }

    public Worker() {
        this(new ClientImpl(), new AsyncEventBus(TAG, new UiThreadExecutor()), new InMemDB());
    }

    public void destroy() {
        Log.i(TAG, "Instance destroyed.");
        client.close();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /************************
     * Server Communication *
     ************************/

    private void receiveMessages(titan.client.messages.Message... msgs) {
        final Message[] nbusyMsgs = DataMaps.getNBusyMessages(msgs);
        List<Message> ml = Arrays.asList(nbusyMsgs);
        // add messages to designated chats
        for (Message msg : nbusyMsgs) {
            userProfile.getChat(msg.chatId).addMessages(ml);
        }
        db.updateMessages(new DB.UpdateMessagesCallback() {
            @Override
            public void messagesUpdated() {
                eventBus.post(new MessagesReceivedEvent(nbusyMsgs));
            }
        }, nbusyMsgs);
    }

    public void sendMessages(final Message... msgs) {
        // handle echo messages separately
        if (Objects.equals(msgs[0].chatId, "echo")) {
            client.echo(msgs[0].body, new EchoCallback() {
                @Override
                public void echoResponse(String msg) {
                    msgs[0] = msgs[0].setStatus(Message.Status.DELIVERED_TO_USER);
                    userProfile.getChat(msgs[0].chatId).updateMessage(msgs[0]);
                    eventBus.post(new MessagesStatusChangedEvent(msgs));
                    receiveMessages(DataMaps.getTitanMessages(msgs));
                }
            });
            return;
        }

        // persist messages in the database with Status = NEW
        db.saveMessages(new DB.SaveMessagesCallback() {
            @Override
            public void messagesSaved() {
                titan.client.messages.Message[] titanMsgs = DataMaps.getTitanMessages(msgs);
                client.sendMessages(new SendMsgsCallback() {
                    @Override
                    public void sentToServer() {
                        // update in memory representation of messages
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = msgs[i].setStatus(Message.Status.SENT_TO_SERVER);
                            userProfile.getChat(msgs[i].chatId).updateMessage(msgs[i]);
                        }

                        // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                        db.updateMessages(new DB.UpdateMessagesCallback() {
                            @Override
                            public void messagesUpdated() {
                                // finally, notify all listening views about the changes
                                eventBus.post(new MessagesStatusChangedEvent(msgs));
                            }
                        }, msgs);
                    }
                }, titanMsgs);
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
        db.getChatMessages(chatId, new DB.GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(List<Message> msgs) {
                userProfile.getChat(chatId).addMessages(msgs);
                eventBus.post(new ChatMessagesRetrievedEvent(chatId));
            }
        });
    }

    /*****************
     * Event Objects *
     *****************/

    public class MessagesReceivedEvent {
        public final Message[] msgs;

        public MessagesReceivedEvent(Message... msgs) {
            this.msgs = msgs;
        }
    }

    public class MessagesStatusChangedEvent {
        public final Message[] msgs;

        public MessagesStatusChangedEvent(Message... msgs) {
            this.msgs = msgs;
        }
    }

    public class UserProfileRetrievedEvent {
    }

    public class ChatMessagesRetrievedEvent {
        public final String chatId;

        public ChatMessagesRetrievedEvent(String chatId) {
            this.chatId = chatId;
        }
    }
}
