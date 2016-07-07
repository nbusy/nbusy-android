package com.nbusy.app.data.callbacks;

import com.nbusy.app.data.UserProfile;

public interface GetProfileCallback {
    void success(UserProfile userProfile);

    void error();
}
