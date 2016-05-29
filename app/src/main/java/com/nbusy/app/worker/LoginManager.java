package com.nbusy.app.worker;

import com.nbusy.app.data.DB;
import com.nbusy.sdk.Client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.messages.MsgMessage;

/**
 * Handles first time login/registration using Google auth.
 */
public class LoginManager implements ConnCallbacks {

    private static final String TAG = ConnManager.class.getSimpleName();
    private final Client client;
    private final DB db;
    private final String googleIDToken;

    public LoginManager(Client client, DB db, String googleIDToken) {
        this.client = client;
        this.db = db;
        this.googleIDToken = googleIDToken;
    }

    @Override
    public void messagesReceived(MsgMessage... msgs) {

    }

    @Override
    public void connected(String reason) {

    }

    @Override
    public void disconnected(String reason) {

    }
}
