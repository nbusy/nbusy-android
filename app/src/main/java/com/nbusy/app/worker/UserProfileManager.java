package com.nbusy.app.worker;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.nbusy.app.InstanceManager;
import com.nbusy.app.activities.LoginActivity;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;

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

    public void createUserProfile(String id, String jwtToken, String email, String name) {

    }

    // retrieves user profile and advertises availability of the user profile with an event
    public void getUserProfile(final Activity activity, boolean force) {
        if (!force && InstanceManager.userProfileRetrieved()) {
            return;
        }

        db.getProfile(new GetProfileCallback() {
            @Override
            public void success(UserProfile prof) {
                Log.i(TAG, "user profile retrieved from DB, starting connection");
                InstanceManager.setUserProfile(prof);
                InstanceManager.getConnManager().ensureConn();
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

}
