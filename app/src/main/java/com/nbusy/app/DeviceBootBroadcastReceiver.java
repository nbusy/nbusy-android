package com.nbusy.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, WorkerService.class);
            serviceIntent.putExtra(WorkerService.STARTED_BY, this.getClass().getSimpleName());
            context.startService(serviceIntent);

            // todo: use WakefulBroadcastReceiver.startWakefulService() instead to make sure that device does not sleep while service is running?
//            /**
//             * This {@code WakefulBroadcastReceiver} takes care of creating and managing a partial wake lock for your app. It passes off the work of
//             * processing the GCM message to an {@code IntentService}, while ensuring that the device does not go back to sleep in the transition.
//             * The {@code IntentService} calls {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to release the wake lock.
//             */
//            public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    // explicitly specify that GcmIntentService will handle the intent
//                    ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
//
//                    // start the service, keeping the device awake while it is launching
//                    startWakefulService(context, (intent.setComponent(comp)));
//                    setResultCode(Activity.RESULT_OK);
//                }
//            }
        }
    }
}
