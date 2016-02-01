package com.nbusy.capacitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for an immutable state object.
 */
public abstract class Container<T> {
    private T state;
    private List<Subscriber<T>> subscribers;

    public Container(T state) {
        this.state = state;
        subscribers = new ArrayList<>();
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
}
