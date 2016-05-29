package com.nbusy.app.data.callbacks;

import com.nbusy.app.data.Profile;

public interface GetProfileCallback {
    void profileRetrieved(Profile userProfile);

    void error();
}
