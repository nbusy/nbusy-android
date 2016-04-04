package com.nbusy.app.worker;

import com.google.common.eventbus.AsyncEventBus;
import com.nbusy.app.data.Config;
import com.nbusy.app.data.InMemDB;
import com.nbusy.sdk.ClientImpl;

/**
 * {@link Worker} single instance provider.
 */
public class WorkerSingleton {
    private static final Config config = new Config();
    private static Worker worker = null;

    public static Worker getWorker() {
        if (worker == null) {
            if (config.env != Config.Env.PRODUCTION) {
                worker = new Worker();
            } else {
                worker = new Worker(new ClientImpl(config.serverUrl), new AsyncEventBus(Worker.class.getSimpleName(), new UiThreadExecutor()), new InMemDB());
            }
        }

        return worker;
    }

    public static void destroyWorker() {
        worker.destroy();
    }
}
