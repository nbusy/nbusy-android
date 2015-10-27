package com.nbusy.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Objects;

public class WorkerService extends Service {

    // whether to terminate after task queue is done or keep running
    private boolean terminateAfterDone;

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
        // terminate the service after queued tasks are done if service was started
        // by device boot event and application is not actively running
        String startedBy = intent.getStringExtra("StartedBy");
        terminateAfterDone = (startedBy != null && Objects.equals(startedBy, "DeviceBootReceiver"));

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return Service.START_STICKY;
    }
}
