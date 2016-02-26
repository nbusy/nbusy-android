package com.nbusy.app;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import java.util.Date;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgCallback;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNDU2MTQ5MjY0LCJ1c2VyaWQiOiIxIn0.wuKJ8CuDkCZYLmhgO-UlZd6v8nxKGk_PtkBwjalyjwA";
    private final Client client;
    private final EventBus eventBus;

    public Worker(final Client client, EventBus eventBus) {
        Log.i(TAG, "Instance created.");
        this.client = client;
        this.eventBus = eventBus;
        client.connect(new ConnCallbacks() {
            @Override
            public void messagesReceived(titan.client.messages.Message[] msgs) {
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
    }

    public Worker() {
        this(new ClientImpl(), new EventBus(TAG));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void simulateSendMessages(final Message[] msgs) {
        class SimulateClient extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                eventBus.post(new MessagesSentEvent(collectMessageIds(msgs)));
            }
        }

        new SimulateClient().execute(null, null, null);
    }

    public void sendMessages(final Message[] msgs) {
        titan.client.messages.Message[] titanMsgs = getTitanMessages(msgs);
        client.sendMessages(titanMsgs, new SendMsgCallback() {
            @Override
            public void sentToServer() {
                eventBus.post(new MessagesSentEvent(collectMessageIds(msgs)));
            }
        });
    }

    public void echo() {

    }

    public void destroy() {
        Log.i(TAG, "Instance destroyed.");
        client.close();
    }

    private titan.client.messages.Message[] getTitanMessages(Message[] msgs) {
        titan.client.messages.Message[] titanMsgs = new titan.client.messages.Message[msgs.length];
        Date now = new Date();
        for (int i = 0; i < msgs.length; i++) {
            titanMsgs[i] = new titan.client.messages.Message(null, msgs[i].to, now, msgs[i].body);
        }
        return titanMsgs;
    }

    private String[] collectMessageIds(Message[] msgs) {
        String[] ids = new String[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            ids[i] = msgs[i].id;
        }
        return ids;
    }

    /*****************
     * Event Objects *
     *****************/

    public class MessagesReceivedEvent {
        public final Message[] msgs;

        public MessagesReceivedEvent(Message[] msgs) {
            this.msgs = msgs;
        }
    }

    public class MessagesSentEvent {
        public final String[] ids;

        public MessagesSentEvent(String[] ids) {
            this.ids = ids;
        }
    }

    public class MessagesDeliveredEvent {
        public final String[] ids;

        public MessagesDeliveredEvent(String[] ids) {
            this.ids = ids;
        }
    }

    public class EchoReceivedEvent {
        public final String message;

        public EchoReceivedEvent(String message) {
            this.message = message;
        }
    }
}
