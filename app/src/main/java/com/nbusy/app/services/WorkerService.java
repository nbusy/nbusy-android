package com.nbusy.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.WorkerSingleton;

/**
 * Hosts {@link Worker} class to ensure continuous operation even when no activity is visible.
 */
public class WorkerService extends Service {

    private static final String TAG = WorkerService.class.getSimpleName();
    public static final String STARTED_BY = "StartedBy";
    private static final int STANDBY_TIME = 3 * 60 * 1000;
    private final StopStandby stopStandby = new StopStandby();
    private final Worker worker = WorkerSingleton.getWorker();
    private int startId;

    class StopStandby extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (worker.needConnection()) {
                try {
                    Thread.sleep(STANDBY_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!stopSelfResult(startId)) {
                Log.e(TAG, "failed to stop service with startId: " + startId + " which did not match the one from the last start request");
            }
        }
    }

    /*********************
     * Service Overrides *
     *********************/

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;

        if (intent != null) {
            String startedBy = intent.getStringExtra(STARTED_BY);
            Log.i(TAG, "Started by: " + startedBy);
        } else {
            // service is restarted by Android system after a termination so intent will be null in this case
            Log.i(TAG, "Restarted by Android system after termination.");
        }

        if (stopStandby.getStatus() != AsyncTask.Status.RUNNING) {
            stopStandby.execute();
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
