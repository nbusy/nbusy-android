package com.nbusy.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nbusy.app.InstanceProvider;
import com.nbusy.app.data.Config;
import com.nbusy.app.worker.Worker;
import com.nbusy.sdk.Client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hosts {@link Worker} class to ensure continuous operation even when no activity is visible.
 */
public class WorkerService extends Service {

    private static final String TAG = WorkerService.class.getSimpleName();
    public static final String STARTED_BY = "StartedBy";
    public static final AtomicBoolean RUNNING = new AtomicBoolean();
    private static final Config config = new Config();
    private final int standbyTime;
    private final StopStandby stopStandby = new StopStandby();
    private final Worker worker = InstanceProvider.getWorker();
    private final Client client = InstanceProvider.getClient();
    private int startId;

    public WorkerService() {
        standbyTime = config.env == Config.Env.PRODUCTION ? 3 * 60 * 1000 : 10 * 1000; // 3 mins (prod) / 10 secs (non-prod)
    }

    class StopStandby extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (worker.needConnection()) {
                try {
                    Thread.sleep(standbyTime);
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
            stopStandby.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        WorkerService.RUNNING.set(true);

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.close();
        RUNNING.set(false);
        Log.i(TAG, "destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
