package net.breezeware.dynamo.android.oauth.model;

public class OAuthToken {
    private String accessToken;

    public OAuthToken(String accessToken) {
        this.accessToken = accessToken;

    }

    public OAuthToken() {
        this.accessToken = accessToken;

    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


}
