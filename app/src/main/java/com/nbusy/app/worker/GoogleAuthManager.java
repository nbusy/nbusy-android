package com.nbusy.app.worker;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.nbusy.app.data.UserProfile;
import com.nbusy.sdk.Client;

import java.util.concurrent.atomic.AtomicBoolean;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Handles first time login/registration using Google auth.
 */
public class GoogleAuthManager implements ConnCallbacks {

    public static final int LOGIN_OK = 9000;
    private static final String TAG = GoogleAuthManager.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper()); // to run callback on UI thread
    private final Client client;
    private final UserProfileManager profileManager;
    private final AtomicBoolean success = new AtomicBoolean(false);

    private String googleIDToken;
    private AuthFinishedCallback cb;
    private Runnable cbSuccess = new Runnable() {
        @Override
        public void run() {
            cb.success();
        }
    };
    private Runnable cbError = new Runnable() {
        @Override
        public void run() {
            cb.error();
        }
    };

    public GoogleAuthManager(Client client, UserProfileManager profileManager) {
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null or empty");
        }
        if (profileManager == null) {
            throw new IllegalArgumentException("profileManager cannot be null or empty");
        }

        this.client = client;
        this.profileManager = profileManager;
    }

    public void login(String googleIDToken, AuthFinishedCallback cb) {
        if (googleIDToken == null || googleIDToken.isEmpty()) {
            throw new IllegalArgumentException("googleIDToken cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        this.googleIDToken = googleIDToken;
        this.cb = cb;
        client.connect(this);
    }

    public interface AuthFinishedCallback {
        void success();

        void error();
    }

    /***************************
     * ConnCallbacks Overrides *
     ***************************/

    @Override
    public void messagesReceived(MsgMessage... msgs) {
    }

    @Override
    public void connected(String reason) {
        boolean requestSent = client.googleAuth(googleIDToken, new GoogleAuthCallback() {
            @Override
            public void success(GoogleAuthResponse res) {
                Log.i(TAG, "Authenticated with NBusy server using Google auth.");
                UserProfile profile = new UserProfile(res.id, res.token, res.email, res.name, res.picture);
                profileManager.createUserProfile(profile, new UserProfileManager.CreateUserProfileCallback() {
                    @Override
                    public void success() {
                        success.set(true);
                        client.close();
                        handler.post(cbSuccess);
                    }

                    @Override
                    public void error() {
                        handler.post(cbError);
                    }
                });
            }

            @Override
            public void fail(int code, String message) {
                Log.i(TAG, "Failed to authenticate with NBusy server using Google auth: " + code + " : " + message);
            }
        });

        if (!requestSent) {
            client.close();
        }
    }

    @Override
    public void disconnected(String reason) {
        // post error if we disconnected during authentication (and not due to successful close of authentication channel)
        if (!success.get()) {
            handler.post(cbError);
        }
    }
}
