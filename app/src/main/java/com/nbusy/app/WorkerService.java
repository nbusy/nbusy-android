package com.nbusy.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WorkerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
