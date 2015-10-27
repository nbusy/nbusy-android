package com.nbusy.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WorkerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // don't allow direct binding to this service
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // todo: initiate connection to nbusy server using neptulon json-rpc java client (or nbusy java client which wraps that and auto-adds all routes)?

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return START_STICKY;
    }
}
