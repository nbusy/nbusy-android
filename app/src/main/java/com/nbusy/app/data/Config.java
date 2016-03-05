package com.nbusy.app.data;

import com.nbusy.app.BuildConfig;

public class Config {
    public final Env env;

    public Config(Env env) {
        this.env = env;
    }

    public Config() {
        this(BuildConfig.DEBUG ? Env.DEBUG : Env.PRODUCTION);
    }

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }
}
