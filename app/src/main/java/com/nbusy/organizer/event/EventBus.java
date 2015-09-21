package com.nbusy.organizer.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {
    private final ExecutorService pool;

    public EventBus(int poolSize) {
        // actually we need to adjust pools based on batch/group sizes
        pool = Executors.newFixedThreadPool(poolSize);
    }
}

// guava async event bus way of doing this...
/*EventBus eventBus = new AsyncEventBus(new Executor() {

    private Handler mHandler;

    @Override
    public void execute(Runnable command) {
        if (mHandler == null) {
        // post events to handlers on the UI thread
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(command);
    }
});*/
