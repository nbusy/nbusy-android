package com.nbusy.app.worker;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.nbusy.app.services.DeviceBootBroadcastReceiver;

import java.util.Objects;

/**
 * Hosts {@link Worker} class to ensure continuous operation even when no activity is visible.
 */
public class WorkerService extends Service {

    // todo: stopSelf() after all queue is done if terminateAfterDone is true
    // todo: stopService() when all queue is done and application is terminated completely (not hidden activities but complete termination, or with a timeout after hidden activities)

    private static final String TAG = WorkerService.class.getSimpleName();
    public static final String STARTED_BY = "StartedBy";
    private final Worker worker = WorkerSingleton.getWorker();
    private boolean terminateAfterDone; // whether to terminate service after task queue is done, or keep running till explicitly destroyed
    private int startId;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;

        // terminate the service after queued tasks are done if service was started
        // by device boot event and application is not actively running
        if (intent != null) {
            String startedBy = intent.getStringExtra(STARTED_BY);
            terminateAfterDone = (startedBy != null && Objects.equals(startedBy, DeviceBootBroadcastReceiver.class.getSimpleName()));
            Log.i(TAG, "Started by: " + startedBy);
        } else {
            // service is restarted by Android system after a termination so intent will be null in this case
            terminateAfterDone = true;
            Log.i(TAG, "Restarted by Android system after termination.");
        }

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroyed.");
        WorkerSingleton.destroyWorker();
    }

    private void processQueue() {
        if (terminateAfterDone && !stopSelfResult(startId)) {
            Log.e(TAG, "Tried to stop service with startId: " + startId + " which did not match the one from the last start request.");
        }
    }

    /*************************
     * Local service binding *
     *************************/

    private final IBinder binder = new WorkerServiceBinder();

    /**
     * Returns an instance of this service so binding components can directly call public methods of this service.
     */
    public class WorkerServiceBinder extends Binder {
        WorkerService getService() {
            return WorkerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        String startedBy = intent.getStringExtra(STARTED_BY);
        Log.i(TAG, "Was bound to by: " + startedBy);

        // allow binding to this local service directly so anyone can call public functions on this service directly
        return binder;
    }
}
