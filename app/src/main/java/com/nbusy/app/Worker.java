package com.nbusy.app;

import com.google.common.eventbus.EventBus;
import com.neptulon.JsonRpc;
import com.neptulon.JsonRpcClient;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {
    private static final String TAG = Worker.class.getSimpleName();
    private final JsonRpc jsonRpc;
    private final EventBus eventBus;

    public Worker(JsonRpc jsonRpc, EventBus eventBus) {
        this.jsonRpc = jsonRpc;
        this.eventBus = eventBus;
    }

    public Worker() {
        this(new JsonRpcClient(), new EventBus(TAG));
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

    public void SendMsg(Message msg) {
        // todo: use AsyncTask here or depend on jsonRpc.SendMsgTask(callback)
        eventBus.post(new StoredMsg(123));
    }

    private void init() {
        // todo: initiate connection to nbusy server using neptulon json-rpc java client (or nbusy java client which wraps that and auto-adds all routes)?
    }
}
