package com.nbusy.app.worker.eventbus;

import com.nbusy.app.data.UserProfile;

public class UserProfileRetrievedEvent {
    public final UserProfile profile;

    public UserProfileRetrievedEvent(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("profile cannot be null");
        }

        this.profile = profile;
    }
}
