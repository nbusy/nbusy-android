package com.nbusy.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nbusy.app.InstanceManager;
import com.nbusy.app.R;
import com.nbusy.app.data.Config;
import com.nbusy.app.worker.LoginManager;

/**
 * Receive ID token for the current Google user.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_GET_TOKEN = 9002;
    private GoogleApiClient googleApiClient;

    // view elements
    private SignInButton signInButton;
    private Button productionModeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setEnabled(true);
        productionModeButton = (Button) findViewById(R.id.production_mode_button);
        if (InstanceManager.getConfig().env != Config.Env.PRODUCTION) {
            productionModeButton.setVisibility(View.VISIBLE);
        }

        // Button click listeners
        signInButton.setOnClickListener(this);
        productionModeButton.setOnClickListener(this);

        // Request only the user's ID token, which can be used to identify the user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult: GET_TOKEN: is success: " + result.getStatus().isSuccess());

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();

                Log.d(TAG, "idToken: " + idToken);
                InstanceManager.getLoginManager().login(idToken, new LoginManager.LoginFinishedCallback() {
                    @Override
                    public void success() {
                        setResult(ChatListActivity.LOGIN_OK);
                        finish();
                    }

                    @Override
                    public void fail() {
                        Log.e(TAG, "Google auth failed");
                        // todo: show a toast notification and ask user to retry
                        signInButton.setEnabled(true);
                    }
                });
            } else {
                Log.e(TAG, "Google auth failed");
                // todo: show a toast notification and ask user to retry
                signInButton.setEnabled(true);
            }
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
                getIdToken();
                break;
            case R.id.production_mode_button:
                productionModeButton.setVisibility(View.GONE);
                setProductionMode();
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

    private void setProductionMode() {
        InstanceManager.setConfig(new Config(Config.Env.PRODUCTION, null));
    }

    @Override
    public void onBackPressed() {
        // can't press back on login as there is nowhere to go
    }
}