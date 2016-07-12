package com.nbusy.app.worker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;

/**
 * Manages connection to the NBusy servers.
 * Also manages connection lifecycle events like sending queued messages on reconnect, etc.
 */
public class ConnManager implements ConnCallbacks {

    private static final String TAG = ConnManager.class.getSimpleName();
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private final UserProfile userProfile;
    private final Context appContext;

    public ConnManager(Client client, EventBus eventBus, DB db, UserProfile userProfile, Context appContext) {
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

        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        this.userProfile = userProfile;
        this.appContext = appContext;
    }

    /**
     * Whether we need an active connection to server.
     */
    public boolean needConnection() {
        // todo: or there are ongoing operations or queued operations or standby timer is still running
        return eventBus.haveSubscribers();
        // todo: start an 3 min disconnect standBy timer on eventbus.unregister in case a view wants to register again or we're in a brief limbo state
    }

    /**
     * Starts/restarts connection sequence to NBusy servers if we are not connected.
     */
    public void ensureConn() {
        if (!client.isConnected()) {
            client.connect(this);
        }

        // start the connection manager service if not running
        if (!ConnManagerService.RUNNING.getAndSet(true)) {
            Intent serviceIntent = new Intent(appContext, ConnManagerService.class);
            serviceIntent.putExtra(ConnManagerService.STARTED_BY, this.getClass().getSimpleName());
            appContext.startService(serviceIntent);
        }
    }

    /***************************
     * ConnCallbacks Overrides *
     ***************************/

    @Override
    public void connected(String reason) {
        Log.i(TAG, "Connected to NBusy server with reason: " + reason);

        boolean called = client.jwtAuth(userProfile.jwtToken, new JWTAuthCallback() {
            @Override
            public void success() {
                Log.i(TAG, "Authenticated with NBusy server using JWT auth.");

                db.getQueuedMessages(new GetChatMessagesCallback() {
                    @Override
                    public void chatMessagesRetrieved(final List<Message> msgs) {
                        if (msgs.isEmpty()) {
                            return;
                        }

                        final Message[] msgsArray = msgs.toArray(new Message[msgs.size()]);
                        client.sendMessages(new SendMsgsCallback() {
                            @Override
                            public void sentToServer() {
                                // update in memory representation of messages
                                final Set<Chat> chats = userProfile.setMessageStatuses(Message.Status.SENT_TO_SERVER, msgsArray);

                                // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                                db.upsertMessages(new UpsertMessagesCallback() {
                                    @Override
                                    public void success() {
                                        Log.i(TAG, "Sent queued messages to server: " + msgs.size());
                                        // finally, notify all listening views about the changes
                                        if (!chats.isEmpty()) {
                                            eventBus.post(new ChatsUpdatedEvent(chats));
                                        }
                                    }

                                    @Override
                                    public void error() {
                                    }
                                }, msgsArray);
                            }
                        }, DataMap.nbusyToTitanMessages(msgsArray));
                    }
                });
            }

            @Override
            public void fail() {
                Log.i(TAG, "Authentication failed with NBusy server using JWT auth.");
            }
        });

        if (!called) {
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
