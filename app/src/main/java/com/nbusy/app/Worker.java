package com.nbusy.app;

import android.os.AsyncTask;

import com.google.common.eventbus.EventBus;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import java.util.Date;

import neptulon.client.callbacks.ConnCallback;
import titan.client.callbacks.RecvMsgsCallback;
import titan.client.callbacks.SendMsgCallback;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private final Client client;
    private final EventBus eventBus;

    public Worker(Client client, EventBus eventBus) {
        this.client = client;
        this.eventBus = eventBus;
        client.connect(new ConnCallback() {
            @Override
            public void connected() {
            }

            @Override
            public void disconnected(String reason) {
            }
        });
    }

    public Worker() {
        this(new ClientImpl("ws://10.0.0.2:3001", new RecvMsgsCallback() {
            @Override
            public void callback(titan.client.messages.Message[] msgs) {
            }
        }), new EventBus(TAG));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void sendMessage(final Message msg) {
        //client.send(msg, callback)
        class SimulateClient extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                eventBus.post(new MessageSentEvent(msg.id));
            }
        }

        new SimulateClient().execute(null, null, null);
    }

    public void sendMessages(Message[] msgs) {
        titan.client.messages.Message[] tmsgs = new titan.client.messages.Message[msgs.length];
        Date now = new Date();
        for (int i = 0; i < msgs.length; i++) {
            tmsgs[i] = new titan.client.messages.Message(null, msgs[i].to, now, msgs[i].body);
        }

        client.sendMessages(tmsgs, new SendMsgCallback() {
            @Override
            public void sentToServer() {
//                eventBus.post(new MessageSentEvent(msg.id));
            }
            @Override
            public void delivered() {

            }
        });
    }



    public void echo() {

    }

    /*****************
     * Event Objects *
     *****************/

    public class MessageSentEvent {
        final String id;

        public MessageSentEvent(String id) {
            this.id = id;
        }
    }

    public class MessageDeliveredEvent {
        final String id;

        public MessageDeliveredEvent(String id) {
            this.id = id;
        }
    }

    public class EchoResponse {
        public final String message;

        public EchoResponse(String message) {
            this.message = message;
        }
    }
}
