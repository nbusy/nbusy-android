package com.nbusy.capacitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Container for an immutable state object.
 */
public abstract class Container<T> {
    private T state;
    private List<Subscriber<T>> subscribers;

    public Container(T state) {
        this.state = state;
        subscribers = new CopyOnWriteArrayList<>();
    }

    public void setState(T state) {
        this.state = state;
        for (Subscriber<T> s : subscribers) {
            s.onStateChange(state);
        }
    }

    public T getState() {
        return state;
    }

    public void subscribe(Subscriber<T> s) {
        subscribers.add(s);
    }

    public void unsubscribe(Subscriber<T> s) {
        subscribers.remove(s);
    }
}
