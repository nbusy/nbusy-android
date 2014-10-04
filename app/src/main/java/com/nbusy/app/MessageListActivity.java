package com.nbusy.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An activity representing a list of Messages. This activity has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched, lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a {@link MessageListFragment} and the item details
 * (if present) is a {@link MessageDetailFragment}.
 * <p/>
 * This activity also implements the required {@link MessageListFragment.Callbacks} interface to listen for item selections.
 */
public class MessageListActivity extends Activity implements MessageListFragment.Callbacks {

  private static final String TAG = "MessageListActivity";
  private static final String PROPERTY_APP_VERSION = "appVersion";
  private static final String PROPERTY_REG_ID = "registration_id";
  GoogleCloudMessaging gcm;
  String SENDER_ID = "218602439235";
  String regId;
  AtomicInteger msgId = new AtomicInteger();
  // whether or not the activity is in two-pane mode, i.e. running on a tablet device
  private boolean mTwoPane;

  /**
   * @return Application's version code from the {@code PackageManager}.
   */
  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
          .detectAll()
          .penaltyLog()
          .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
          .penaltyDeath()
          .build());
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message_list);

    if (findViewById(R.id.message_detail_container) != null) {
      // the detail container view will be present only in the large-screen layouts (res/values-large and
      // res/values-sw600dp). If this view is present, then the activity should be in two-pane mode
      mTwoPane = true;

      // in two-pane mode, list items should be given the 'activated' state when touched
      ((MessageListFragment) getFragmentManager()
          .findFragmentById(R.id.message_list))
          .setActivateOnItemClick(true);
    }

    // GCM registration
    gcm = GoogleCloudMessaging.getInstance(this);
    regId = getRegistrationId(getApplicationContext());
    if (regId.isEmpty()) {
      registerInBackground();
    }
  }

  /**
   * Callback method from {@link MessageListFragment.Callbacks} indicating that the item with the given ID was selected.
   */
  @Override
  public void onItemSelected(String id) {
    if (mTwoPane) {
      // in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction
      Bundle arguments = new Bundle();
      arguments.putString(MessageDetailFragment.ARG_ITEM_ID, id);
      MessageDetailFragment fragment = new MessageDetailFragment();
      fragment.setArguments(arguments);
      getFragmentManager()
          .beginTransaction()
          .replace(R.id.message_detail_container, fragment)
          .commit();
    } else {
      // in single-pane mode, simply start the detail activity for the selected item ID
      Intent detailIntent = new Intent(this, MessageDetailActivity.class);
      detailIntent.putExtra(MessageDetailFragment.ARG_ITEM_ID, id);
      startActivity(detailIntent);
    }
  }

  /**
   * Gets the current registration ID for application on GCM service, if there is one. If result is empty, the app needs to register.
   *
   * @return registration ID, or empty string if there is no existing registration ID.
   */
  private String getRegistrationId(Context context) {
    final SharedPreferences prefs = getGcmPreferences(context);
    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i(TAG, "Registration not found.");
      return "";
    }

    // check if app was updated; if so, it must clear the registration ID since the existing regID is not guaranteed to work with
    // the new app version
    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = getAppVersion(context);
    if (registeredVersion != currentVersion) {
      Log.i(TAG, "App version changed.");
      return "";
    }
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
        String msg = "";
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
    final SharedPreferences prefs = getGcmPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.apply();
  }

  /**
   * @return Application's {@code SharedPreferences}.
   */
  private SharedPreferences getGcmPreferences(Context context) {
    // this sample app persists the registration ID in shared preferences, but how you store the regID in your app is up to you
    return getSharedPreferences(MessageListActivity.class.getSimpleName(), Context.MODE_PRIVATE);
  }

  /**
   * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
   * Not needed for this demo since the device sends upstream messages to a server that echoes back the message using the 'from'
   * address in the message.
   */
  private void sendRegistrationIdToBackend() {
  }

// Send an upstream message.
//    public void onClick(final View view) {
//        if (view == findViewById(R.id.send)) {
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    String msg = "";
//                    try {
//                        Bundle data = new Bundle();
//                        data.putString("my_message", "Hello World");
//                        data.putString("my_action", "com.nbusy.app.ECHO_NOW");
//                        String id = Integer.toString(msgId.incrementAndGet());
//                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
//                        msg = "Sent message";
//                    } catch (IOException ex) {
//                        msg = "Error :" + ex.getMessage();
//                    }
//                    return msg;
//                }
//
//                @Override
//                protected void onPostExecute(String msg) {
//                    mMessageDisplay.append(msg + "\n");
//                }
//            }.execute(null, null, null);
//        } else if (view == findViewById(R.id.clear)) {
//            mMessageDisplay.setText("");
//        }
//    }
}
