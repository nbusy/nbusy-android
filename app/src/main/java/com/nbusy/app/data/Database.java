package com.nbusy.app.data;

public interface Database {
    void getProfile(GetProfileCallback cb);

    interface GetProfileCallback {
        void profileRetrieved(Profile userProfile);
    }
}
