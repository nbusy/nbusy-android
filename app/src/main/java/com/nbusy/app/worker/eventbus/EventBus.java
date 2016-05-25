package com.nbusy.app.worker.eventbus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus extends com.google.common.eventbus.EventBus {
    private final List<Object> subscribers = new CopyOnWriteArrayList<>();

    // todo: remove following class if we don't need this:
//    public EventBus() {
//         this(new AsyncEventBus(TAG, new UIThreadExecutor()));
//    }


    @Override
    public void register(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        // todo: instead of below, publish SubscriberRegisteredEvent

        // a view is attaching to event bus so we need to ensure connectivity
//        if (!client.isConnected()) {
//            client.connect(connCallbacks);
//        }

        // start the worker service if not running
//        if (!WorkerService.RUNNING.get()) {
//            Intent serviceIntent = new Intent(c, WorkerService.class);
//            serviceIntent.putExtra(WorkerService.STARTED_BY, o.getClass().getSimpleName());
//            c.startService(serviceIntent);
//        }

        subscribers.add(o);
        super.register(o);
    }

    public void unregister(Object o) {
        subscribers.remove(o);
        super.unregister(o);
        // todo: start 3 min standBy timer here in case a view wants to register again or we're in a brief limbo state
    }

    public boolean haveSubscribers() {
        return !subscribers.isEmpty();
    }
}
