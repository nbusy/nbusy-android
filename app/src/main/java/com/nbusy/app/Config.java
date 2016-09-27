package com.nbusy.app;

import android.util.Log;

public class Config {

    private static final String TAG = Config.class.getSimpleName();
    public final Env env;
    public final String serverUrl;
    public final int standbyTime;

    /**
     * Server URL should start with ws:// or wss://
     */
    public Config(Env env, String serverUrl) {
        this.env = env;
        this.serverUrl = serverUrl;

        // 3 mins (prod) / 10 secs (non-prod)
        standbyTime = env == Env.PRODUCTION ? 3 * 60 * 1000 : 10 * 1000;

        logConfig();
    }

    public Config() {
        String envENV = System.getenv("NBUSY_ENV");
        String envENV2 = System.getenv("ENV");
        if (envENV != null) {
            env = Env.valueOf(envENV);
        } else if(envENV2 != null) {
            env = Env.valueOf(envENV2);
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

        // 3 mins (prod) / 10 secs (non-prod)
        standbyTime = env == Env.PRODUCTION ? 3 * 60 * 1000 : 10 * 1000;

        logConfig();
    }

    private void logConfig() {
        Log.i(TAG, String.format("initialized with Env: %s, Server URL: %s, Standby Time: %s(s)", env, serverUrl, standbyTime / 1000));
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}