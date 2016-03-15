package com.nbusy.app.data;

import com.nbusy.app.BuildConfig;

public class Config {
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
            // todo: nbusy client itself should know these server urls
            serverUrl = "wss://nbusy.herokuapp.com";
        } else {
            serverUrl = "wss://nbusy.herokuapp.com";
        }
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}
