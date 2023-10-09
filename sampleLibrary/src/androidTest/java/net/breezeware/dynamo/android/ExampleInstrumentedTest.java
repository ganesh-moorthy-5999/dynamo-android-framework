package net.breezeware.dynamo.android;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import net.breezeware.dynamo.android.oauth.model.TokenIdentifier;
import net.breezeware.dynamo.android.oauth.service.impl.OAuthServiceImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    OAuthServiceImpl oAuthService = new OAuthServiceImpl();

    @Before
    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("net.breezeware.dynamo.android.test", appContext.getPackageName());
        SharedPreferences sharedPreferences = appContext.getSharedPreferences("com.dynamo.OAuth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("com.dynamo.OAuth Expiry_Time", 1509900856);
        editor.apply();
    }

    @Test
    public void isAccessTokenExpiredTest() {
        TokenIdentifier tokenIdentifier = new TokenIdentifier("com.dynamo.OAuth");
        Assert.assertTrue(oAuthService.isAccessTokenExpired(tokenIdentifier, appContext));
    } 
}