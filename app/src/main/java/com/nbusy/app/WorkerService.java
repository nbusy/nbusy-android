package com.nbusy.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Objects;

public class WorkerService extends Service {

    private boolean running;

    private boolean startedByBoot;

    @Override
    public IBinder onBind(Intent intent) {
        // don't allow direct binding to this service
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // todo: initiate connection to nbusy server using neptulon json-rpc java client (or nbusy java client which wraps that and auto-adds all routes)?
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // we want this service to continue running until it is explicitly stopped, so return sticky
        int startType = Service.START_STICKY;

        if (running) {
            return startType;
        }

        running = true;

        String startedBy = intent.getStringExtra("StartedBy");
        if (startedBy != null && Objects.equals(startedBy, "DeviceBootReceiver")) {
            startedByBoot = true;
        }

        return startType;
    }
}
