package com.soygul.organizer.data;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.LruCache;

public class DataService extends Service {
    private LruCache<String, Bitmap> imageCache;

    private ApiClient apiClient;

    public DataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
