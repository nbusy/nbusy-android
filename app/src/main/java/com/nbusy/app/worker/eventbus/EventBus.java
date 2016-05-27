package com.nbusy.app.worker.eventbus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event bus for passing information between activities and other components.
 * Activities need an event bus and can't depend on regular callbacks due to their life-cycle.
 */
public class EventBus extends com.google.common.eventbus.EventBus {
    private final List<Object> subscribers = new CopyOnWriteArrayList<>();

    @Override
    public void register(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        subscribers.add(o);
        super.register(o);
    }

    public void unregister(Object o) {
        subscribers.remove(o);
        super.unregister(o);
    }

    public boolean haveSubscribers() {
        return !subscribers.isEmpty();
    }
}
