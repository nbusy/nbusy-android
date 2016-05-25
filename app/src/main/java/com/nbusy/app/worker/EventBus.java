package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;

import com.nbusy.app.services.WorkerService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final String TAG = EventBus.class.getSimpleName();

    private final com.google.common.eventbus.EventBus eventBus;
    private final List<Object> subscribers = new CopyOnWriteArrayList<>();

    public EventBus(com.google.common.eventbus.EventBus eventBus) {
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }

        this.eventBus = eventBus;
    }

    public EventBus() {

// todo: remove following class if we don't need this:
// this(new AsyncEventBus(TAG, new UIThreadExecutor()));

        this(new com.google.common.eventbus.EventBus(TAG));
    }

    /**
     * Event bus reg/unreg.
     */
    public void register(Object o, Context c) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (c == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        // todo: instead of below, publish SubscriberRegisteredEvent
        // a view is attaching to event bus so we need to ensure connectivity
//        if (!client.isConnected()) {
//            client.connect(connCallbacks);
//        }

        // start the worker service if not running
        if (!WorkerService.RUNNING.get()) {
            Intent serviceIntent = new Intent(c, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, o.getClass().getSimpleName());
            c.startService(serviceIntent);
        }

        subscribers.add(o);
        eventBus.register(o);
    }

    public void unregister(Object o) {
        subscribers.remove(o);
        eventBus.unregister(o);
        // todo: start 3 min standBy timer here in case a view wants to register again or we're in a brief limbo state
    }
}
