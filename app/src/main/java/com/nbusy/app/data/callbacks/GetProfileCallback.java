package com.nbusy.app.data.callbacks;

import com.nbusy.app.data.UserProfile;

public interface GetProfileCallback {
    void profileRetrieved(UserProfile userProfile);

    void error();
}
