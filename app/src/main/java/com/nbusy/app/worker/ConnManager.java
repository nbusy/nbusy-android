package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.nbusy.app.Config;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMap;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;
import com.nbusy.app.services.ConnManagerService;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.Client;

import java.util.List;
import java.util.Set;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.JWTAuthCallback;
import titan.client.messages.MsgMessage;

/**
 * Manages connection to the NBusy servers.
 * Also manages connection lifecycle events like sending queued messages on reconnect, etc.
 */
public class ConnManager implements ConnCallbacks {

    private static final String TAG = ConnManager.class.getSimpleName();
    private final KeepAliveTimer keepAliveTimer = new KeepAliveTimer();
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private final UserProfile userProfile;
    private final Context appContext;
    private final Worker worker;
    private final Config config;

    public ConnManager(Client client, EventBus eventBus, DB db, UserProfile userProfile, Context appContext, Worker worker, Config config) {
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null");
        }
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }
        if (appContext == null) {
            throw new IllegalArgumentException("appContext cannot be null");
        }
        if (worker == null) {
            throw new IllegalArgumentException("worker cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        this.userProfile = userProfile;
        this.appContext = appContext;
        this.worker = worker;
        this.config = config;
    }

    /**
     * Keeps the connection manager service alive as long as we need a live connection to the server.
     * When we don't need a connection, stops the service.
     */
    class KeepAliveTimer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // keep this service running, as long as we need an open connection to the server
            while (needConnection()) {
                while (needConnection()) {
                    try {
                        Thread.sleep(config.standbyTime / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // check-wait-check cycle
                try {
                    Thread.sleep(config.standbyTime / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            appContext.stopService(new Intent(appContext, ConnManagerService.class));
            client.close();
        }
    }

    /**
     * Whether we need an active connection to server.
     */
    public boolean needConnection() {
        return eventBus.haveSubscribers() || client.haveOngoingRequests();
    }

    /**
     * Starts/restarts connection sequence to NBusy servers if we are not connected.
     * @param requestedBy - If provided, this will be used in logs as the initiator of the connection manager service.
     */
    public synchronized void ensureConn(String requestedBy) {
        if (!client.isConnected()) {
            client.connect(this);
        }

        // start the connection timer if not running
        if (keepAliveTimer.getStatus() != AsyncTask.Status.RUNNING) {
            keepAliveTimer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        // start the connection manager service if not running
        if (!ConnManagerService.RUNNING.getAndSet(true)) {
            Intent serviceIntent = new Intent(appContext, ConnManagerService.class);
            serviceIntent.putExtra(ConnManagerService.STARTED_BY, this.getClass().getSimpleName() + ", upon request by: " + requestedBy);
            appContext.startService(serviceIntent);
        }
    }

    /***************************
     * ConnCallbacks Overrides *
     ***************************/

    @Override
    public void connected(String reason) {
        Log.i(TAG, "Connected to NBusy server with reason: " + reason);

        boolean requestSent = client.jwtAuth(userProfile.jwtToken, new JWTAuthCallback() {
            @Override
            public void success() {
                Log.i(TAG, "Authenticated with NBusy server using JWT auth.");

                db.getQueuedMessages(new GetChatMessagesCallback() {
                    @Override
                    public void chatMessagesRetrieved(final List<Message> msgs) {
                        if (msgs.isEmpty()) {
                            return;
                        }

                        worker.sendMessages(msgs);
                    }
                });
            }

            @Override
            public void fail() {
                Log.i(TAG, "Authentication failed with NBusy server using JWT auth.");
            }
        });

        if (!requestSent) {
            client.close();
        }
    }

    @Override
    public void disconnected(String reason) {
        Log.w(TAG, "Connection attempt OR connection to NBusy server was closed with reason: " + reason);
    }

    @Override
    public void messagesReceived(MsgMessage... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMap.titanToNBusyMessages(msgs);
        final Set<Chat> chats = userProfile.upsertMessages(nbusyMsgs);
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void success() {
                for (Chat chat : chats) {
                    eventBus.post(new ChatsUpdatedEvent(chat));
                }
            }

            @Override
            public void error() {
            }
        }, nbusyMsgs);
    }
}
