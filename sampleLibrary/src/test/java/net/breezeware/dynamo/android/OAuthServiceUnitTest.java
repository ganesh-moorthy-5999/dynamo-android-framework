package net.breezeware.dynamo.android;

import net.breezeware.dynamo.android.oauth.model.OAuthServerDetails;
import net.breezeware.dynamo.android.oauth.model.TokenIdentifier;
import net.breezeware.dynamo.android.oauth.model.UserDetails;
import net.breezeware.dynamo.android.oauth.service.impl.OAuthServiceImpl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class OAuthServiceUnitTest {
    OAuthServiceImpl oAuthService = new OAuthServiceImpl();

    @Test
    public void isValidParametersTest() {
        UserDetails userDetails = new UserDetails("guru@breezeware.net", "");
        OAuthServerDetails oAuthServerDetails = new OAuthServerDetails("se", "ss", "ss", "ss");
        TokenIdentifier tokenIdentifier = new TokenIdentifier("com");
        Assert.assertFalse(oAuthService.isValidParameters(userDetails, oAuthServerDetails, tokenIdentifier));
    }

}