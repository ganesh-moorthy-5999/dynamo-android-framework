package net.breezeware.dynamo.android.oauth.model;

public class TokenIdentifier {

    private String sharedPreferencesPackageName;

    public TokenIdentifier(String sharedPreferencesPackageName) {
        this.sharedPreferencesPackageName = sharedPreferencesPackageName;
    }

    public String getSharedPreferencesPackageName() {
        return sharedPreferencesPackageName;
    }

    public void setSharedPreferencesPackageName(String sharedPreferencesPackageName) {
        this.sharedPreferencesPackageName = sharedPreferencesPackageName;
    }
}
