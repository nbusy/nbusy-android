package com.nbusy.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nbusy.app.activities.LoginActivity;
import com.nbusy.app.data.Config;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.InMemDB;
import com.nbusy.app.data.Profile;
import com.nbusy.app.data.sqldb.SQLDB;
import com.nbusy.app.worker.ConnManager;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;
import com.nbusy.sdk.Client;
import com.nbusy.sdk.ClientImpl;

/**
 * Creator and keeper of all the instances.
 * Values are initialized on first request.
 * All instances are singletons unless otherwise mentioned.
 */
public class InstanceProvider extends Application {
    private static final String TAG = InstanceProvider.class.getSimpleName();
    private static Context appContext;
    private static Config config;
    private static Worker worker;
    private static ConnManager connManager;
    private static Client client;
    private static EventBus eventBus;
    private static DB db;
    private static Profile userProfile;

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        // todo: could this be done by ProfileManager or ConnManager or DBManager or CacheManager or Profile should manage DB and be domain object ?
        getDB().getProfile(new DB.GetProfileCallback() {
            @Override
            public void profileRetrieved(Profile prof) {
                Log.i(TAG, "user profile retrieved");
                userProfile = prof;
                getConnManager().startConnection();
                getEventBus().post(new UserProfileRetrievedEvent(prof));
            }

            @Override
            public void error() {
                Log.i(TAG, "user profile does not exist, starting login activity");
                // no profile stored so display login activity
                Intent intent = new Intent(appContext, LoginActivity.class);
                appContext.startActivity(intent);
            }
        });
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

    public static synchronized Worker getWorker() {
        if (worker == null) {
            worker = new Worker(getAppContext(), getClient(), getEventBus(), getDB(), getUserProfile());
        }

        return worker;
    }

    public static synchronized ConnManager getConnManager() {
        if (connManager == null) {
            connManager = new ConnManager(getClient(), getEventBus(), getDB(), getAppContext(), getUserProfile());
        }

        return connManager;
    }

    public static synchronized Client getClient() {
        if (client == null) {
            if (getConfig().serverUrl != null) {
                client = new ClientImpl(getConfig().serverUrl, false);
            } else {
                client = new ClientImpl();
            }
        }

        return client;
    }

    public static synchronized EventBus getEventBus() {
        if (eventBus == null) {
            // todo: remove UIThreadExecutor if we don't need this any more
            eventBus = new EventBus(/*new AsyncEventBus(TAG, new UIThreadExecutor())*/);
        }

        return eventBus;
    }

    public static synchronized DB getDB() {
        if (db == null) {
            if (getConfig().env == Config.Env.PRODUCTION) {
                db = new SQLDB();
            } else {
                db = new InMemDB();
            }
        }

        return db;
    }

    public static synchronized Profile getUserProfile() {
        if (userProfile == null) {
            throw new IllegalStateException("userProfile is not retrieved yet or does not exist");
        }

        return userProfile;
    }
}
