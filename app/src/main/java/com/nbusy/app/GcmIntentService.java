package com.nbusy.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message. {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver})
 * holds a partial wake lock for this service while the service does its work. When the service is finished, it calls {@code completeWakefulIntent()}
 * to release the wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "NBusy GCM client";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent); // the getMessageType() intent parameter must be the intent you received in your BroadcastReceiver

        if (!extras.isEmpty()) {  // has effect of un-parcelling Bundle
            // filter messages based on message type. Since it is likely that GCM will be extended in the future with new message types,
            // just ignore any message types you're not interested in, or that you don't recognize
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    sendNotification("Send error: " + extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    sendNotification("Deleted messages on server: " + extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    // this loop represents the service doing some work
                    for (int i = 0; i < 5; i++) {
                        Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    // post notification of received message
                    Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                    sendNotification("Received: " + extras.toString());
                    Log.i(TAG, "Received: " + extras.toString());
                    break;
            }
        }

        // release the wake lock provided by the WakefulBroadcastReceiver
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // put the message into a notification and post it
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MessageListActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
