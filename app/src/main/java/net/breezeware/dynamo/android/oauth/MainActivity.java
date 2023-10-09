package net.breezeware.dynamo.android.oauth;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import net.breezeware.dynamo.R;
import net.breezeware.dynamo.android.oauth.exception.OAuthServiceException;
import net.breezeware.dynamo.android.oauth.model.OAuthServerDetails;
import net.breezeware.dynamo.android.oauth.model.OAuthToken;
import net.breezeware.dynamo.android.oauth.model.TokenIdentifier;
import net.breezeware.dynamo.android.oauth.model.UserDetails;
import net.breezeware.dynamo.android.oauth.service.api.OAuthService;
import net.breezeware.dynamo.android.oauth.service.impl.OAuthServiceImpl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserLoginSync userLoginSync = new UserLoginSync();
        userLoginSync.execute();
    }


    @SuppressLint("StaticFieldLeak")
    public class UserLoginSync extends AsyncTask<Void, Void, Void> {
        OAuthService oauthService = new OAuthServiceImpl();
        String serverUrl = "https://refresh.health:8443/oauth/token";
        String clientId = "dynamo-oauth2-client";
        String clientSecret = "karthik";
        String grantType = "password";
        String userName = "guru@breezeware.net";
        String password = "guru@123";

        OAuthServerDetails oauthServerDetails = new OAuthServerDetails(serverUrl, clientId, clientSecret, grantType);
        UserDetails userDetails = new UserDetails(userName, password);
        TokenIdentifier tokenIdentifier = new TokenIdentifier("com.dynamo.OAuth");

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OAuthToken authToken = oauthService.retrieveOAuthToken(userDetails, oauthServerDetails, tokenIdentifier,
                        getApplicationContext());
                Log.i("Access Token", authToken.getAccessToken());
            } catch (OAuthServiceException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}