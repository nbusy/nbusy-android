package com.nbusy.app.worker.eventbus;

import com.nbusy.app.InstanceManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * Event bus for passing information between activities and other components.
 * Activities need an event bus and can't depend on regular callbacks due to their life-cycle.
 */
public class EventBus extends com.google.common.eventbus.AsyncEventBus {
    private final List<Object> subscribers = new CopyOnWriteArrayList<>();

    public EventBus(Executor executor) {
        super(executor);
    }

    @Override
    public void register(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        // a view is attaching to event bus so we need to ensure connectivity
        if (InstanceManager.userProfileRetrieved()) {
            InstanceManager.getConnManager().ensureConn(this.getClass().getSimpleName());
        }

        if (subscribers.contains(o)) {
            return;
        }

        subscribers.add(o);
        super.register(o);
    }

    public void unregister(Object o) {
        if (!subscribers.contains(o)) {
            throw new IllegalStateException("object was not registered");
        }

        subscribers.remove(o);
        super.unregister(o);
    }

    public boolean haveSubscribers() {
        return !subscribers.isEmpty();
    }
}
