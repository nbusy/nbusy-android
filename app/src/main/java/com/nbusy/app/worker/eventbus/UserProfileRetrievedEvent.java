package com.nbusy.app.worker.eventbus;

import com.nbusy.app.data.Profile;

public class UserProfileRetrievedEvent {
    public final Profile profile;

    public UserProfileRetrievedEvent(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("profile cannot be null");
        }

        this.profile = profile;
    }
}
