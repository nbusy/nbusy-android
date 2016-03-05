package com.nbusy.app.data;

import com.nbusy.app.BuildConfig;

public class Config {
    public final Env env;
    public final String serverUrl;

    public Config(Env env, String serverUrl) {
        this.env = env;
        this.serverUrl = serverUrl;
    }

    public Config() {
        this(BuildConfig.DEBUG ? Env.DEBUG : Env.PRODUCTION, "wss://nbusy.com");
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}
