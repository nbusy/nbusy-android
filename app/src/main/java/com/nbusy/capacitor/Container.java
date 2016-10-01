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
        if (this.state == state) {
            throw new IllegalArgumentException("you need to provide a new copy of the immutable state object to set a new state");
        }
        if (this.state.equals(state)) {
            throw new IllegalArgumentException("old and the new state are the same");
        }

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

    public synchronized boolean haveSubscribers() {
        return !subscribers.isEmpty();
    }
}
