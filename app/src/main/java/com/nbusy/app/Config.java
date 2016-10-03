package com.nbusy.app;

import android.util.Log;

import com.google.common.base.Optional;

public class Config {

    private static final String TAG = Config.class.getSimpleName();
    public final Env env;
    public final String serverUrl;
    public final int standbyTime;

    public enum Env {
        DEBUG,
        TEST,
        PRODUCTION
    }

    /**
     * Server URL should start with ws:// or wss://
     */
    public Config(Env env, String serverUrl) {
        this(Optional.of(env), Optional.of(serverUrl));
    }

    public Config() {
        this(Optional.<Env>absent(), Optional.<String>absent());
    }

    private Config(Optional<Env> env, Optional<String> serverUrl) {
        if (env.isPresent()) {
            this.env = env.get();
        } else {
            String envENV = System.getenv("NBUSY_ENV");
            String envENV2 = System.getenv("ENV");
            if (envENV != null) {
                this.env = Env.valueOf(envENV);
            } else if(envENV2 != null) {
                this.env = Env.valueOf(envENV2);
            } else {
                this.env = BuildConfig.DEBUG ? Env.DEBUG : Env.PRODUCTION;
            }
        }

        if (serverUrl.isPresent()) {
            this.serverUrl = serverUrl.get();
        } else {
            String envURL = System.getenv("NBUSY_SERVER_URL");
            if (envURL != null) {
                this.serverUrl = envURL;
            } else {
                switch (this.env) {
                    case DEBUG:
                        this.serverUrl = "ws://10.0.2.2:3000"; // android emulator/adb host machine
                        break;
                    case TEST:
                        this.serverUrl = "ws://127.0.0.1:3001"; // local test server
                        break;
                    default:
                        this.serverUrl = "wss://nbusy.herokuapp.com";
                        break;
                }
            }
        }

        // 4 mins (prod) / 15 secs (non-prod)
        standbyTime = this.env == Env.PRODUCTION ? 4 * 60 * 1000 : 15 * 1000;

        Log.i(TAG, String.format("initialized with Env: %s, Server URL: %s, Standby Time: %s(s)", env, serverUrl, standbyTime / 1000));
    }
}
