package com.nbusy.app;

import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

import neptulon.client.ConnImpl;
import neptulon.client.ResHandler;
import neptulon.client.Response;

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
    }

    public Worker() {
        this(new ClientImpl(), new EventBus(TAG));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void sendMessage(Message msg) {
        eventBus.post(new MessageSavedEvent(msg.id, msg.from, msg.body, msg.sent, msg.owner));
        eventBus.post(new MessageSentEvent(msg.id, msg.from, msg.body, msg.sent, msg.owner));
    }

    private void init() {
        // TODO: remove this test code
        ConnImpl conn = new ConnImpl();
        conn.connect();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        class Test {
            final String message;

            Test(String message) {
                this.message = message;
            }
        }

        conn.sendRequest("test", new Test("wow"), new ResHandler<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public void handler(Response<String> res) {
                Log.i(TAG, "Received response: " + res.result);
            }
        });
    }

    /*****************
     * Event Objects *
     *****************/

    public class MessageSavedEvent extends Message {
        MessageSavedEvent(String id, String from, String body, String sent, boolean owner) {
            super(id, from, body, sent, owner);
        }
    }

    public class MessageSentEvent extends Message {
        MessageSentEvent(String id, String from, String body, String sent, boolean owner) {
            super(id, from, body, sent, owner);
            sentToServer = true;
        }
    }

    public class MessageDeliveredEvent extends Message {
        MessageDeliveredEvent(String id, String from, String body, String sent, boolean owner) {
            super(id, from, body, sent, owner);
            sentToServer = true;
            delivered = true;
        }
    }
}
