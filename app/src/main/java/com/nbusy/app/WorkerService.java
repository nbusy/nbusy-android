package com.nbusy.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.neptulon.JsonRpc;
import com.neptulon.JsonRpcClient;

import java.util.Objects;

public class WorkerService extends Service {

    private final JsonRpc jsonRpc = new JsonRpcClient();

    public final static String STARTED_BY = "StartedBy";

    private final IBinder binder = new WorkerServiceBinder();

    private final LocalBroadcastManager lbm = null; // send results back to caller from background threads..

    // whether to terminate after task queue is done or keep running
    private boolean terminateAfterDone;

    @Override
    public IBinder onBind(Intent intent) {
        // allow binding to this local service directly so anyone can call public functions on this service directly
        return binder;
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
        String startedBy = intent.getStringExtra(STARTED_BY);
        terminateAfterDone = (startedBy != null && Objects.equals(startedBy, DeviceBootBroadcastReceiver.class.getSimpleName()));

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return Service.START_STICKY;
    }

    /**
     * Returns an instance of this service so binding components can directly call public methods of this service.
     */
    public class WorkerServiceBinder extends Binder {
        WorkerService getService() {
            return WorkerService.this;
        }
    }

    public boolean getRunning() {
        return true;
    }

    // todo: stopSelf() after all queue is done if terminateAfterDone is true
    // todo: stopService() when all queue is done and application is terminated completely (not hidden activities but complete termination, or with a timeout after hidden activities)
}
