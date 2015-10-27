package com.nbusy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, WorkerService.class);
            i.putExtra("StartedBy", "DeviceBootReceiver");
            context.startService(i);
        }
    }
}
