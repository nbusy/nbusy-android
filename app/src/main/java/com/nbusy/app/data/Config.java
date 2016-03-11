package com.nbusy.app.data;

import com.nbusy.app.BuildConfig;

public class Config {
    public static final String[] SERVERS = new String[] {"nbusy.com:80", "nbusy.com:3000"};

    public final Env env;
    public final String serverUrl;

    /**
     * Server URL should start with ws:// or wss://
     */
    public Config(Env env, String serverUrl) {
        this.env = env;
        this.serverUrl = serverUrl;
    }

    public Config() {
        env = BuildConfig.DEBUG ? Env.DEBUG : Env.PRODUCTION;
        if (env == Env.PRODUCTION) {
            serverUrl = "wss://" + SERVERS[0];
        } else {
            serverUrl = "ws://" + SERVERS[0];
        }
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}
