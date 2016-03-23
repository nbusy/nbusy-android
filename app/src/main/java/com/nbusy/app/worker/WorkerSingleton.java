package com.nbusy.app.worker;

import com.google.common.eventbus.EventBus;
import com.nbusy.app.data.Config;
import com.nbusy.app.data.InMemDB;
import com.nbusy.sdk.ClientImpl;

/**
 * {@link Worker} single instance provider.
 */
public class WorkerSingleton {
    private static Worker worker = null;
    private static Config config = new Config();

    public static Worker getWorker() {
        if (worker == null) {
            if (config.env != Config.Env.PRODUCTION) {
                worker = new Worker();
            } else {
                worker = new Worker(new ClientImpl(config.serverUrl), new EventBus(), new InMemDB());
            }
        }

        return worker;
    }

    public static void destroyWorker() {
        worker.destroy();
    }
}
