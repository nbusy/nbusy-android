package com.nbusy.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nbusy.app.InstanceManager;
import com.nbusy.app.Config;
import com.nbusy.app.worker.ConnManager;
import com.nbusy.sdk.Client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hosts {@link ConnManager} class to ensure continuous operation even when no activity is visible.
 */
public class ConnManagerService extends Service {

    private static final String TAG = ConnManagerService.class.getSimpleName();
    public static final String STARTED_BY = "StartedBy";
    public static final AtomicBoolean RUNNING = new AtomicBoolean();

    /*********************
     * Service Overrides *
     *********************/

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String startedBy = intent.getStringExtra(STARTED_BY);
            Log.i(TAG, "Started by: " + startedBy);
        } else {
            // service is restarted by Android system after a termination so intent will be null in this case
            Log.i(TAG, "Restarted by Android system after termination.");
        }

        ConnManagerService.RUNNING.set(true);

        // we want this service to continue running until it is explicitly stopped, so return sticky
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RUNNING.set(false);
        Log.i(TAG, "destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
