package com.nbusy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, DeviceBootBroadcastReceiver.class.getSimpleName());
            context.startService(serviceIntent);
        }
    }
}
