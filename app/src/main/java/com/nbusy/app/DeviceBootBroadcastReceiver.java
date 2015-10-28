package com.nbusy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, WorkerService.class);
            i.putExtra(WorkerService.STARTED_BY, DeviceBootBroadcastReceiver.class.getSimpleName());
            context.startService(i);
        }
    }
}
