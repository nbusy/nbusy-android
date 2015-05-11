package com.nbusy.organizer.task;

import com.nbusy.organizer.enums.TaskType;

/**
 * A task that requires network connectivity. If there is no network connectivity, this job will be
 * saved to disk and will be run as soon as network connection is established.
 */
public class NetworkTask extends Task {
    /*public void CheckNetwork(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            // display error
        }
    }*/
    public NetworkTask(TaskType type) {
        super(type);
    }

    @Override
    public void onQueued() {
    }

    @Override
    public void onRunning() {
    }
}
