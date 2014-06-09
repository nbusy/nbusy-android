package com.nbusy.app.task;

/**
 * A task that requires network connectivity.
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

    @Override
    public void onQueued() {
    }
}
