package com.nbusy.app;

import android.app.Application;
import android.content.Context;

import com.nbusy.app.data.Config;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.ClientImpl;

/**
 * Creator and keeper of all the instances.
 * Values are initialized on first request.
 */
public class InstanceProvider extends Application {
    private static final Config config = new Config();
    private static Context appContext;
    private static Worker worker;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

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
