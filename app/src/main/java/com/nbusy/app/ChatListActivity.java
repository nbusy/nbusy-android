package com.nbusy.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nbusy.sdk.titan.neptulon.ConnImpl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An activity representing a list of Chats. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChatDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ChatListFragment} and the item details
 * (if present) is a {@link ChatDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ChatListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ChatListActivity extends Activity implements ChatListFragment.Callbacks {

    private static final String TAG = ChatListActivity.class.getSimpleName();
    private static final String PROPERTY_APP_VERSION = "appVer";
    private static final String PROPERTY_REG_ID = "regId";
    private static final String SENDER_ID = "218602439235";
    private final AtomicInteger msgId = new AtomicInteger();
    private final Worker worker = WorkerSingleton.getWorker(); // todo: this needs to be done by NBusyApplication to be deterministic (but can we access network during Application.onCreate?)
    private GoogleCloudMessaging gcm;
    private String regId;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start the worker service
        Intent serviceIntent = new Intent(this, WorkerService.class);
        serviceIntent.putExtra(WorkerService.STARTED_BY, this.getClass().getSimpleName());
        this.startService(serviceIntent);

        // set view(s)
        setContentView(R.layout.activity_chat_list);

        if (findViewById(R.id.chat_detail_container) != null) {
            // the detail container view will be present only in the large-screen layouts (res/values-large and res/values-sw600dp)
            // if this view is present, then the activity should be in two-pane mode
            mTwoPane = true;

            // in two-pane mode, list items should be given the 'activated' state when touched.
            ((ChatListFragment) getFragmentManager()
                    .findFragmentById(R.id.chat_list))
                    .setActivateOnItemClick(true);
        }

        // GCM registration
        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(this);
        if (regId.isEmpty()) {
            registerInBackground();
        }

        sendGcmMessage("just testing from Android simulator");
        sendGcmMessage("test 2");
        sendGcmMessage("test 3");

        ConnImpl conn = new ConnImpl();
    }

    /**
     * Callback method from {@link ChatListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // in two-pane mode, show the detail view in this activity by adding
            // or replacing the detail fragment using a fragment transaction
            Bundle arguments = new Bundle();
            arguments.putString(ChatDetailFragment.ARG_ITEM_ID, id);
            ChatDetailFragment fragment = new ChatDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chat_detail_container, fragment)
                    .commit();
        } else {
            // in single-pane mode, simply start the detail activity for the selected item ID
            Intent detailIntent = new Intent(this, ChatDetailActivity.class);
            detailIntent.putExtra(ChatDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e); // should never happen
        }
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "GCM registration not found.");
            return "";
        }

        // check if app was updated; if so, it must clear the registration ID since the existing
        // regID is not guaranteed to work with the new app version
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed. GCM registration will be refreshed.");
            return "";
        }

        Log.i(TAG, "GCM registration id found: " + registrationId);
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and the app versionCode in the application's shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // you should send the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send messages to your app
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send upstream messages to a server that echo back the message
                    // using the 'from' address in the message.

                    // persist the regID - no need to register again
                    storeRegistrationId(getApplicationContext(), regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // if there is an error, don't just keep trying to register
                    // require the user to click a button again, or perform exponential back-off
                }

                Log.i(TAG, msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    private void sendRegistrationIdToBackend() {
        sendGcmMessage(regId);
    }

    private void sendGcmMessage(final String message) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    Bundle data = new Bundle();
                    data.putString("test_message_from_device", message);
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent upstream GCM message to the backend: " + message;
                } catch (IOException ex) {
                    msg = "Error while sending upstream GCM message to the backend:" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }
}
