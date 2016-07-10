package com.nbusy.app.worker;

import android.util.Log;

import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.sdk.Client;

import java.util.ArrayList;
import java.util.Date;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Handles first time login/registration using Google auth.
 */
public class GoogleAuthManager implements ConnCallbacks {

    public static final int LOGIN_OK = 9000;
    private static final String TAG = ConnManager.class.getSimpleName();
    private final Client client;
    private final DB db;
    private String googleIDToken;
    private AuthFinishedCallback cb;

    public GoogleAuthManager(Client client, DB db) {
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null or empty");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null or empty");
        }

        this.client = client;
        this.db = db;
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
        void fail();
    }

    /***************************
     * ConnCallbacks Overrides *
     ***************************/

    @Override
    public void messagesReceived(MsgMessage... msgs) {
    }

    @Override
    public void connected(String reason) {
        boolean called = client.googleAuth(googleIDToken, new GoogleAuthCallback() {
            @Override
            public void success(GoogleAuthResponse res) {
                Log.i(TAG, "Authenticated with NBusy server using Google auth.");

                ArrayList<Chat> chats = new ArrayList<>();
                chats.add(new Chat("echo", "Echo", "Yo!", new Date()));
                UserProfile prof = new UserProfile(res.id, res.token, res.email, res.name, res.picture, chats);

                db.createProfile(prof, new CreateProfileCallback() {
                    @Override
                    public void success() {
                        client.close();
                        cb.success();
                    }

                    @Override
                    public void error() {
                        Log.e(TAG, "Failed to create user profile");
                        cb.fail();
                    }
                });
            }

            @Override
            public void fail(int code, String message) {
                Log.i(TAG, "Failed to authenticate with NBusy server using Google auth: " + code + " : " + message);
            }
        });

        if (!called) {
            client.close();
        }
    }

    @Override
    public void disconnected(String reason) {
        // todo: restart login dialog with a disconnect message ?
    }
}
