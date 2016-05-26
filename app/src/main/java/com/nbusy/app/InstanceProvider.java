package com.nbusy.app;

import com.nbusy.app.data.Config;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.ClientImpl;

/**
 * Creator and keeper of all the instances.
 */
public class InstanceProvider {
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
}
