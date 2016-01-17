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
    }

    public Worker() {
        this(new ClientImpl(), new EventBus(TAG));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /* events */
    public class StoredMsg {
        public final int msgId;

        public StoredMsg(int msgId) {
            this.msgId = msgId;
        }
    }
    /* events */

    public void sendMsg(Message msg) {
        // todo: use AsyncTask here or depend on neptulonJsonRpc.SendMsgTask(callback)
        eventBus.post(new StoredMsg(123));
    }

    private void init() {
        // todo: initiate connection to nbusy server using neptulon json-rpc java client (or nbusy java client which wraps that and auto-adds all routes)?
    }
}
