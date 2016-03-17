package com.nbusy.capacitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Container for an immutable state object.
 */
public abstract class Container<T> {
    private final List<Subscriber<T>> subscribers = new CopyOnWriteArrayList<>();
    private T state;

    public Container(T initialState) {
        state = initialState;
    }

    public synchronized void setState(T state) {
        this.state = state;
        for (Subscriber<T> s : subscribers) {
            s.onStateChange(state);
        }
    }

    public synchronized T getState() {
        return state;
    }

    public synchronized void subscribe(Subscriber<T> s) {
        subscribers.add(s);
    }

    public synchronized void unsubscribe(Subscriber<T> s) {
        subscribers.remove(s);
    }
}
