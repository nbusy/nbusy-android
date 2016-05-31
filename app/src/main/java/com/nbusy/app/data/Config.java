package com.nbusy.app.data;

import android.util.Log;

import com.nbusy.app.BuildConfig;

public class Config {

    private static final String TAG = Config.class.getSimpleName();
    public final Env env;
    public final String serverUrl;

    /**
     * Server URL should start with ws:// or wss://
     */
    public Config(Env env, String serverUrl) {
        this.env = env;
        this.serverUrl = serverUrl;
        Log.i(TAG, String.format("initialized with Env: %s, Server URL: %s", env, serverUrl));
    }

    public Config() {
        String envENV = System.getenv("NBUSY_ENV");
        if (envENV != null) {
            env = Env.valueOf(envENV);
        } else {
            env = BuildConfig.DEBUG ? Env.DEBUG : Env.PRODUCTION;
        }

        String envURL = System.getenv("NBUSY_SERVER_URL");
        if (envURL != null) {
            serverUrl = envURL;
        } else {
            switch (env) {
                case DEBUG:
                    serverUrl = "ws://10.0.2.2:3000"; // android emulator/adb host machine
                    break;
                case TEST:
                    serverUrl = "ws://127.0.0.1:3001"; // local test server
                    break;
                default:
                    serverUrl = null; // determined by NBusy SDK
                    break;
            }
        }

        Log.i(TAG, String.format("initialized with Env: %s, Server URL: %s", env, serverUrl));
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}
