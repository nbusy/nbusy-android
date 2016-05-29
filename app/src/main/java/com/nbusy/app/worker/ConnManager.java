package com.nbusy.app.worker;

import android.util.Log;

import com.nbusy.app.InstanceManager;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMap;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.Profile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;
import com.nbusy.sdk.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.callbacks.JWTAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Manages connection to the NBusy servers.
 * Also manages connection lifecycle events like sending queued messages on reconnect, etc.
 */
public class ConnManager implements ConnCallbacks {
    private static final String TAG = ConnManager.class.getSimpleName();
    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private Profile userProfile;
    private String googleIDToken;

    /**
     * Initializes connection manager with previously acquired JWT token stored in the user profile.
     */
    public ConnManager(Client client, EventBus eventBus, DB db, Profile userProfile) {
        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        this.userProfile = userProfile;
    }

    /**
     * Initializes connection manager with Google ID token.
     */
    public ConnManager(Client client, EventBus eventBus, DB db, String googleIDToken) {
        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        this.googleIDToken = googleIDToken;
    }

    @Override
    public void connected(String reason) {
        Log.i(TAG, "Connected to NBusy server with reason: " + reason);

        // todo: check return == false from jwt/google auth calls in case connection drops in between calls (highly unlikely though)

        if (userProfile == null) {
            client.googleAuth(googleIDToken, new GoogleAuthCallback() {
                @Override
                public void success(GoogleAuthResponse res) {
                    Log.i(TAG, "Authenticated with NBusy server using Google auth.");

                    final Profile prof = new Profile(res.id, res.token, res.email, res.name, res.picture, new ArrayList<Chat>());
                    db.createProfile(prof, new CreateProfileCallback() {
                        @Override
                        public void success() {
                            InstanceManager.setUserProfile(prof);
                            InstanceManager.getEventBus().post(new UserProfileRetrievedEvent(prof));
                        }

                        @Override
                        public void error() {
                            Log.e(TAG, "Failed to create user profile");
                        }
                    });
                }

                @Override
                public void fail(int code, String message) {
                    Log.i(TAG, "Failed to authenticate with NBusy server using Google auth: " + code + " : " + message);
                }
            });

            return;
        }

        client.jwtAuth(userProfile.jwttoken, new JWTAuthCallback() {
            @Override
            public void success() {
                Log.i(TAG, "Authenticated with NBusy server using JWT auth.");

                db.getQueuedMessages(new GetChatMessagesCallback() {
                    @Override
                    public void chatMessagesRetrieved(final List<Message> msgs) {
                        final Message[] msgsArray = msgs.toArray(new Message[msgs.size()]);
                        client.sendMessages(new SendMsgsCallback() {
                            @Override
                            public void sentToServer() {
                                // update in memory representation of messages
                                final Set<Chat> chats = userProfile.setMessageStatuses(Message.Status.SENT_TO_SERVER, msgsArray);

                                // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                                db.upsertMessages(new UpsertMessagesCallback() {
                                    @Override
                                    public void messagesUpserted() {
                                        Log.i(TAG, "Sent queued messages to server: " + msgs.size());
                                        // finally, notify all listening views about the changes
                                        if (!chats.isEmpty()) {
                                            eventBus.post(new ChatsUpdatedEvent(chats));
                                        }
                                    }
                                }, msgsArray);
                            }
                        }, DataMap.getTitanMessages(msgsArray));
                    }
                });
            }

            @Override
            public void fail() {
                Log.i(TAG, "Authentication failed with NBusy server using JWT auth.");
            }
        });
    }

    @Override
    public void disconnected(String reason) {
        Log.w(TAG, "Connection attempt OR connection to NBusy server was shut down with reason: " + reason);
    }

    @Override
    public void messagesReceived(MsgMessage... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMap.getNBusyMessages(msgs);
        final Set<Chat> chats = userProfile.upsertMessages(nbusyMsgs);
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void messagesUpserted() {
                for (Chat chat : chats) {
                    eventBus.post(new ChatsUpdatedEvent(chat));
                }
            }
        }, nbusyMsgs);
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

        // start the worker service if not running // todo: this must happen after we have a conn?
//        if (!WorkerService.RUNNING.get()) {
//            Intent serviceIntent = new Intent(InstanceManager.getAppContext(), WorkerService.class);
//            serviceIntent.putExtra(WorkerService.STARTED_BY, this.getClass().getSimpleName());
//            InstanceManager.getAppContext().startService(serviceIntent);
//        }
    }
}
