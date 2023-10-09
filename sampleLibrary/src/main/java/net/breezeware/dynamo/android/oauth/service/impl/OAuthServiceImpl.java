package net.breezeware.dynamo.android.oauth.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import net.breezeware.dynamo.android.oauth.exception.OAuthServiceException;
import net.breezeware.dynamo.android.oauth.model.OAuthServerDetails;
import net.breezeware.dynamo.android.oauth.model.OAuthToken;
import net.breezeware.dynamo.android.oauth.model.TokenIdentifier;
import net.breezeware.dynamo.android.oauth.model.UserDetails;
import net.breezeware.dynamo.android.oauth.service.api.OAuthService;

import java.io.IOException;
import java.util.Calendar;

import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthError;
import ca.mimic.oauth2library.OAuthResponse;

public class OAuthServiceImpl implements OAuthService {

    public final String tag = "OAuthService";

    @Override
    public OAuthToken retrieveOAuthToken(UserDetails userDetails,
                                         OAuthServerDetails serverDetails,
                                         TokenIdentifier tokenIdentifier,
                                         @NonNull Context context) throws OAuthServiceException {
        Log.i(tag, "Entering retrieveAndStoreOAuthToken");
        boolean isValidArguments = isValidParameters(userDetails, serverDetails, tokenIdentifier);
        if (isValidArguments) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    tokenIdentifier.getSharedPreferencesPackageName(), Context.MODE_PRIVATE);
            String existingAccessToken = sharedPreferences.getString(tokenIdentifier.getSharedPreferencesPackageName()
                    + "ACCESS_TOKEN", null);
            if (existingAccessToken != null) {
                Log.i(tag, "Access Token already exists");
                boolean tokenExpired = isAccessTokenExpired(tokenIdentifier, context);
                if (tokenExpired) {
                    Log.i(tag, "Access Token is expired");
                    String existingRefreshToken = sharedPreferences.getString(
                            tokenIdentifier.getSharedPreferencesPackageName() + "REFRESH_TOKEN", null);
                    if (existingRefreshToken != null) {
                        serverDetails.setGrantType("refresh_token");
                        return updateAndStoreAccessToken(userDetails, serverDetails,
                                tokenIdentifier,
                                existingRefreshToken, context);
                    }
                } else {
                    //if token does not expires returning existing
                    Log.i(tag, "Access Token does not expired");
                    Log.i(tag, "Leaving retrieveAndStoreOAuthToken");
                    OAuthToken existingOAuthToken = new OAuthToken();
                    existingOAuthToken.setAccessToken(existingAccessToken);
                    return existingOAuthToken;
                }
            } else {
                // if there is no existing data we are initiating Login Process
                return initiateLoginProcessAndStoreOAuthToken(serverDetails, userDetails, tokenIdentifier, context);
            }
        } else {
            throw new OAuthServiceException("One of the parameters received is empty");
        }
        // returning empty token
        return new OAuthToken();
    }

    private boolean isValidParameters(UserDetails userDetails, OAuthServerDetails serverDetails,
                                      TokenIdentifier tokenIdentifier) {
        Log.i(tag, "Entering isValidArguments ()");
        //checking all the arguments passed are not empty
        boolean isValidUserDetails = ((userDetails.getUserName().length() > 1)
                && userDetails.getUserPassword().length() > 1);

        boolean isValidServerDetails = ((serverDetails.getClientID().length() > 1
                && serverDetails.getClientSecret().length() > 1
                && serverDetails.getGrantType().length() > 1
                && serverDetails.getServerUrl().length() > 1));

        boolean isValidTokenIdentifier = (tokenIdentifier.getSharedPreferencesPackageName().length() > 1);

        //returns false if anyone parameter is empty
        Log.i(tag, "Leaving isValidArguments()");
        return isValidServerDetails && isValidUserDetails && isValidTokenIdentifier;

    }

    private OAuthToken initiateLoginProcessAndStoreOAuthToken(OAuthServerDetails serverDetails,
                                                              UserDetails userDetails,
                                                              TokenIdentifier tokenIdentifier,
                                                              Context context) throws OAuthServiceException {
        Log.i(tag, "Entering initiateLoginProcessAndStoreOAuthToken ()");
        OAuthResponse oauthResponse;
        OAuth2Client.Builder builder = new OAuth2Client.Builder(serverDetails.getClientID(),
                serverDetails.getClientSecret(),
                serverDetails.getServerUrl())
                .grantType(serverDetails.getGrantType())
                .username(userDetails.getUserName())
                .password(userDetails.getUserPassword());
        OAuth2Client client = builder.build();
        try {
            oauthResponse = client.requestAccessToken();
        } catch (IOException e) {
            Log.i(tag, "IOException during initiateLoginProcessAndStoreOAuthToken() = " + e.toString());
            throw new OAuthServiceException(e.toString());
        }
        if (oauthResponse != null) {
            if (oauthResponse.isSuccessful()) {
                Log.i(tag, "Leaving initiateLoginProcessAndStoreOAuthToken with accessToken "
                        + oauthResponse.getAccessToken());
                return storeOAuthToken(oauthResponse, tokenIdentifier, context);
            } else {
                OAuthError error = oauthResponse.getOAuthError();
                String errorMsg = error.getError();
                Log.i(tag, "Error during initiateLoginProcessAndStoreOAuthToken() : "
                        + oauthResponse.getBody() + errorMsg);
                throw new OAuthServiceException(oauthResponse.getBody());
            }
        } else {
            Log.i(tag, "Leaving initiate LoginProcess And StoreOAuthToken without accessToken");
            throw new OAuthServiceException("Expected error please try again");
        }
    }

    private OAuthToken storeOAuthToken(OAuthResponse oauthResponse, TokenIdentifier tokenIdentifier, Context context) {
        Log.i(tag, "Entering storeOAuthToken ()");
        OAuthToken authToken = new OAuthToken();
        String accessToken = oauthResponse.getAccessToken();
        String refreshToken = oauthResponse.getRefreshToken();
        authToken.setAccessToken(accessToken);
        Log.i(tag, " Access Token = " + accessToken + "  Refresh Token = " + refreshToken);
        Calendar calendar = Calendar.getInstance();
        int sec = calendar.get(Calendar.SECOND);
        Long expiryTime = oauthResponse.getExpiresIn();
        Log.i(tag, "Access Token exp in" + oauthResponse.getExpiresIn() + "sec");
        calendar.set(Calendar.SECOND, sec + expiryTime.intValue());
        long accessTokenExpiryTime = calendar.getTimeInMillis();
        Log.i("expiryTime in ", String.valueOf(accessTokenExpiryTime));
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                tokenIdentifier.getSharedPreferencesPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenIdentifier.getSharedPreferencesPackageName() + "ACCESS_TOKEN",
                authToken.getAccessToken());
        editor.putString(tokenIdentifier.getSharedPreferencesPackageName() + "REFRESH_TOKEN", refreshToken);
        editor.putLong(tokenIdentifier.getSharedPreferencesPackageName() + "EXPIRY_TIME", accessTokenExpiryTime);
        editor.apply();
        Log.i(tag, "Leaving storeOAuthToken ()");
        return authToken;
    }

    private OAuthToken updateAndStoreAccessToken(UserDetails userDetails,
                                                 OAuthServerDetails serverDetails,
                                                 TokenIdentifier tokenIdentifier,
                                                 String existingRefreshToken,
                                                 @NonNull Context appContext) throws OAuthServiceException {
        Log.i(tag, "Entering updateAndStoreAccessToken()");
        OAuthResponse response;
        OAuthToken authToken = new OAuthToken();
        try {
            OAuth2Client.Builder builder = new OAuth2Client.Builder(serverDetails.getClientID(),
                    serverDetails.getClientSecret(),
                    serverDetails.getServerUrl())
                    .grantType(serverDetails.getGrantType());
            OAuth2Client client = builder.build();
            response = client.refreshAccessToken(existingRefreshToken);
        } catch (IOException e) {
            Log.i(tag, "IOException during updateAndStoreOAuthToken() = " + e.toString());
            throw new OAuthServiceException(e.toString());
        }
        if (response != null) {
            if (response.isSuccessful()) {
                Log.i(tag, "Leaving updateAndStoreAccessToken() with accessToken" + response.getAccessToken());
                return storeOAuthToken(response, tokenIdentifier, appContext);
            } else {
                OAuthError error = response.getOAuthError();
                String errorMsg = error.getError();
                Log.i(tag, "Error during updateAndStoreAccessToken() : " + response.getBody() + errorMsg);
                //String res = response.getBody();
                //if refresh token is also expired we are updating the server details grant type as "password" and
                // initiating the Login process
                serverDetails.setGrantType("password");
                return initiateLoginProcessAndStoreOAuthToken(serverDetails, userDetails, tokenIdentifier, appContext);
            }
        } else {
            Log.d(tag, "Response is null in updateAndStoreAccessToken()");
            Log.i(tag, "Leaving updateAndStoreAccessToken() without accessToken");
            return authToken;
        }
    }

    @Override
    public boolean inValidOAuthToken(TokenIdentifier tokenIdentifier, Context context) {
        Log.i(tag, "Entering inValidOAuthToken()");
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                tokenIdentifier.getSharedPreferencesPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        Log.i(tag, "Leaving inValidOAuthToken()");
        return true;
    }

    private boolean isAccessTokenExpired(TokenIdentifier tokenIdentifier, Context context) {
        Log.i(tag, "Entering isAccessTokenExpired()");
        Log.i(tag, context.getPackageName());
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                tokenIdentifier.getSharedPreferencesPackageName(), Context.MODE_PRIVATE);
        long expiryTime = sharedPreferences.getLong(tokenIdentifier.getSharedPreferencesPackageName()
                        + "EXPIRY_TIME",
                0);
        Calendar calendar = Calendar.getInstance();
        if (expiryTime != 0) {
            Log.i(tag, "Leaving isAccessTokenExpired()");
            Log.i(tag, "ExpiryTime " + expiryTime + "current time" + calendar.getTimeInMillis());
            return calendar.getTimeInMillis() > expiryTime;
        } else {
            Log.d(tag, "Expiry time is not valid");
        }
        Log.i(tag, "Leaving isAccessTokenExpired()");
        return false;
    }
}
