package com.nbusy.app.worker;

import com.nbusy.app.data.Config;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.worker.eventbus.EventBus;
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
                worker = new Worker(new ClientImpl(), new EventBus(), new InMemDB());
            } else {
                worker = new Worker(new ClientImpl(config.serverUrl, true), new EventBus(), new InMemDB());
            }
        }

        return worker;
    }

    public static void destroyWorker() {
        worker.destroy();
    }
}
