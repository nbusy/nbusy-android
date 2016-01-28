package com.nbusy.app;

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

    public void sendMessage(Message msg) {
        eventBus.post(new MessageSavedEvent(msg.id, msg.from, msg.body, msg.sent, msg.owner));
//client.send();

        eventBus.post(new MessageSentEvent(msg.id, msg.from, msg.body, msg.sent, msg.owner));
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
