package com.nbusy.app;

import android.app.Application;
import android.content.Context;

import com.nbusy.app.data.Config;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.sqldb.SQLDB;
import com.nbusy.app.worker.ConnManager;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

/**
 * Creator and keeper of all the instances.
 * Values are initialized on first request.
 * All instances are singletons unless otherwise mentioned.
 */
public class InstanceProvider extends Application {
    private static final Config config = new Config();
    private static Context appContext;
    private static Worker worker;
    private static ConnManager connManager;
    private static Client client;
    private static EventBus eventBus;
    private static DB db;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static synchronized Worker getWorker() {
        if (worker == null) {
            worker = new Worker(getClient(), getEventBus(), getDB());
        }

        return worker;
    }

    public static synchronized ConnManager getConnManager() {
        if (connManager == null) {
            connManager = new ConnManager(getClient(), getEventBus(), getDB(), getAppContext(), getWorker().userProfile.get());
        }

        return connManager;
    }

    public static synchronized Client getClient() {
        if (client == null) {
            if (config.serverUrl != null) {
                client = new ClientImpl(config.serverUrl, false);
            } else {
                client = new ClientImpl();
            }
        }

        return client;
    }

    public static synchronized EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }

        return eventBus;
    }

    public static synchronized DB getDB() {
        if (db == null) {
            if (config.env == Config.Env.PRODUCTION) {
                db = new SQLDB();
            } else {
                db = new InMemDB();
            }
        }

        return db;
    }
}
