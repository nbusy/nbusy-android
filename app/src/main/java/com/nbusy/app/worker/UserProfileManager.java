package com.nbusy.app.worker;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.InstanceManager;
import com.nbusy.app.activities.LoginActivity;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;

import java.util.ArrayList;
import java.util.Date;

/**
 * Manages initialization of the user profile
 */
public class UserProfileManager {

    private static final String TAG = UserProfileManager.class.getSimpleName();
    private final EventBus eventBus;
    private final DB db;

    public UserProfileManager(EventBus eventBus, DB db) {
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }

        this.eventBus = eventBus;
        this.db = db;
    }

    // retrieves user profile and advertises availability of the user profile with an event
    public void getUserProfile(final Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("activity cannot be null");
        }

        db.getProfile(new GetProfileCallback() {
            @Override
            public void success(UserProfile prof) {
                Log.i(TAG, "user profile retrieved from DB, starting connection");
                InstanceManager.setUserProfile(prof);
                InstanceManager.getConnManager().ensureConn(this.getClass().getSimpleName()); // we need this here since eventBus.register() skips .ensureConn() if user profile is empty
                eventBus.post(new UserProfileRetrievedEvent(prof));
            }

            @Override
            public void error() {
                Log.i(TAG, "user profile does not exist in DB, starting login activity");
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivityForResult(intent, GoogleAuthManager.LOGIN_OK);
            }
        });
    }

    public interface CreateUserProfileCallback {
        void success();

        void error();
    }

    public void createUserProfile(UserProfile profile, final CreateUserProfileCallback cb) {
        // prepare default echo bot chat
        String chatId = "echo";
        String chatPeer = "echo";
        String lastMessage = "Greetings stranger!";

        Message greetingMsg = Message.newIncomingMessage(chatId, chatPeer, lastMessage, new Date());
        Chat echoChat = new Chat(chatId, chatPeer, lastMessage, new Date(), ImmutableSet.of(greetingMsg));
        ArrayList<Chat> chats = new ArrayList<>();
        chats.add(echoChat);

        profile.upsertChats(chats);

        db.createProfile(profile, new CreateProfileCallback() {
            @Override
            public void success() {
                Log.i(TAG, "Created user profile");
                cb.success();
            }

            @Override
            public void error() {
                Log.e(TAG, "Failed to create user profile");
                cb.error();
            }
        });
    }
}
