package com.nbusy.app;

import android.os.AsyncTask;

import com.google.common.eventbus.EventBus;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

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
        client.connect();
    }

    public Worker() {
        this(new ClientImpl(), new EventBus(TAG));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void sendMessage(final Message msg) {
        //client.send(msg, callback)
        class SimulateClient extends AsyncTask {
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
}
