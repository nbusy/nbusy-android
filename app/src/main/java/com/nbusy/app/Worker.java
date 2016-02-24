package com.nbusy.app;

import android.os.AsyncTask;
import android.util.Log;

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
                Log.i(TAG, "Worker connected to NBusy server.");
            }

            @Override
            public void disconnected(String reason) {
                Log.w(TAG, "Worker failed to connect to NBusy server.");
            }
        });
    }

    public Worker() {
        this(new ClientImpl("ws://10.0.0.2:3001", new RecvMsgsCallback() {
            @Override
            public void callback(titan.client.messages.Message[] msgs) {
                // todo: eventBus.post(new MessagesReceivedEvent())
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
                eventBus.post(new MessagesSentEvent(new String[]{msg.id}));
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
                // todo: eventBus.post(new MessagesSentEvent(msg.id));
            }
        });
    }

    public void echo() {

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
