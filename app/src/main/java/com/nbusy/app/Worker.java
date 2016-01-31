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
        //client.send(msg, callback)
        eventBus.post(new MessageSentEvent(msg.id));
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
