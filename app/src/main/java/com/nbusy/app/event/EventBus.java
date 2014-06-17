package com.nbusy.app.event;


public class EventBus {
}

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
