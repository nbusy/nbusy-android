package com.nbusy.app;

import android.app.Application;
import android.content.Context;

import com.nbusy.app.data.DB;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.sqldb.SQLDB;
import com.nbusy.app.worker.ConnManager;
import com.nbusy.app.worker.LoginManager;
import com.nbusy.app.worker.UserProfileManager;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UIThreadExecutor;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

/**
 * Creator and keeper of all the instances.
 * Values are initialized on first request.
 * All instances are singletons unless otherwise mentioned.
 */
public class InstanceManager extends Application {

    private static Context appContext;
    private static Config config;
    private static Worker worker;
    private static ConnManager connManager;
    private static LoginManager loginManager;
    private static Client client;
    private static EventBus eventBus;
    private static DB db;
    private static UserProfile userProfile;
    private static UserProfileManager userProfileManager;

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public synchronized static Context getAppContext() {
        return appContext;
    }

    public static synchronized Config getConfig() {
        if (config == null) {
            config = new Config();
        }

        return config;
    }

    public static synchronized void setConfig(Config cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        config = cfg;
    }

    public static synchronized Worker getWorker() {
        if (worker == null) {
            worker = new Worker(getClient(), getEventBus(), getDB(), getUserProfile());
        }

        return worker;
    }

    public static synchronized ConnManager getConnManager() {
        if (connManager == null) {
            connManager = new ConnManager(getClient(), getEventBus(), getDB(), getUserProfile(), getAppContext());
        }

        return connManager;
    }

    public static synchronized LoginManager getLoginManager() {
        if (loginManager == null) {
            loginManager = new LoginManager(getClient(), getDB());
        }

        return loginManager;
    }

    public static synchronized Client getClient() {
        if (client == null) {
            if (getConfig().serverUrl != null) {
                // todo: always use async client otherwise we'll get android.os.NetworkOnMainThreadException, which only happens on TLS mode !
                client = new ClientImpl(getConfig().serverUrl, true);
            } else {
                client = new ClientImpl(true);
            }
        }

        return client;
    }

    public static synchronized EventBus getEventBus() {
        if (eventBus == null) {
            // todo: we need this as some events are raised on non-UI threads which then update the UI and cause an exception
            // though this approach is not the best either as it creates one thread per event invocation, which we don't need
            eventBus = new EventBus(new UIThreadExecutor());
        }

        return eventBus;
    }

    public static synchronized DB getDB() {
        if (db == null) {
            db = new SQLDB(getAppContext());
        }
        if (!db.isOpen()) {
            db = new SQLDB(getAppContext());
        }

        return db;
    }

    public static synchronized void setUserProfile(UserProfile prof) {
        if (userProfile != null) {
            throw new IllegalStateException("userProfile has already been initialized");
        }

        userProfile = prof;
    }

    public static synchronized UserProfile getUserProfile() {
        if (userProfile == null) {
            throw new IllegalStateException("userProfile is not retrieved yet or does not exist");
        }

        return userProfile;
    }

    public static synchronized boolean userProfileRetrieved() {
        return userProfile != null;
    }

    public static synchronized UserProfileManager getUserProfileManager() {
        if (userProfileManager == null) {
            userProfileManager = new UserProfileManager(getEventBus(), getDB());
        }

        return userProfileManager;
    }
}
