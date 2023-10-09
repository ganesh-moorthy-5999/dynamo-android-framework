package net.breezeware.dynamo.android.oauth.service.api;

import android.content.Context;

import net.breezeware.dynamo.android.oauth.exception.OAuthServiceException;
import net.breezeware.dynamo.android.oauth.model.OAuthServerDetails;
import net.breezeware.dynamo.android.oauth.model.OAuthToken;
import net.breezeware.dynamo.android.oauth.model.TokenIdentifier;
import net.breezeware.dynamo.android.oauth.model.UserDetails;

public interface OAuthService {

    /**
     * Retrieve AccessToken to the user.
     *
     * @param userDetails     User Details comprises userName and userPassword.
     * @param serverDetails   Server Details comprises serverUrl, clientId, clientSecret and grantType.
     * @param tokenIdentifier Token Identifier comprises the location in Shared Preferences.
     * @param context         Application context is required to use SharedPreferences.
     * @return OAuthToken
     * @throws OAuthServiceException If provided parameters is empty or If unexpected error happens exception is thrown.
     */
    OAuthToken retrieveOAuthToken(UserDetails userDetails, OAuthServerDetails serverDetails,
                                  TokenIdentifier tokenIdentifier, Context context) throws OAuthServiceException;

    boolean inValidOAuthToken(TokenIdentifier tokenIdentifier, Context context);

}
