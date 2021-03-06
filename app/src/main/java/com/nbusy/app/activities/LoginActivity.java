package com.nbusy.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.nbusy.app.InstanceManager;
import com.nbusy.app.R;
import com.nbusy.app.worker.GoogleAuthManager;

/**
 * Receive ID token for the current Google user.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_GET_TOKEN = 9002;
    private final GoogleAuthManager googleAuthManager = InstanceManager.getGoogleAuthManager();
    private GoogleApiClient googleApiClient;

    private SignInButton signInButton;
    private TextView statusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setEnabled(true);
        statusTextView = (TextView) findViewById(R.id.status);
        statusTextView.setText("");

        // Button click listeners
        signInButton.setOnClickListener(this);

        // Request only the user's ID token, which can be used to identify the user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to make an additional call to personalize your application.
        String clientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .requestProfile()
                .build();

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested.
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != RC_GET_TOKEN) {
            return;
        }

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        final Status status = result.getStatus();
        Log.d(TAG, "onActivityResult: GET_TOKEN: is success: " + status.isSuccess());

        if (status.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();

            Log.d(TAG, "idToken: " + idToken);
            googleAuthManager.login(idToken, new GoogleAuthManager.AuthFinishedCallback() {
                @Override
                public void success() {
                    setResult(GoogleAuthManager.LOGIN_OK);
                    finish();
                }

                @Override
                public void error() {
                    Log.e(TAG, "Google auth failed with status: " + status);
                    signInButton.setEnabled(true);
                    statusTextView.setText(getString(R.string.login_error));
                }
            });
        } else {
            Log.e(TAG, "Google auth failed with status: " + status);
            signInButton.setEnabled(true);
            statusTextView.setText(getString(R.string.login_error));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signInButton.setEnabled(false);
                statusTextView.setText(getString(R.string.logging_in));
                getIdToken();
                break;
        }
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
        Log.d(TAG, "getIDToken: starting to get id token");
    }

    @Override
    public void onBackPressed() {
        // can't press back on login as there is nowhere to go
    }
}