package net.breezeware.dynamo.android.oauth.model;

public class OAuthServerDetails {
    private String serverUrl;
    private String clientID;
    private String clientSecret;
    private String grantType;

    /**
     * This is the constructor for OAuthServerDetails.
     *
     * @param serverUrl    serverUrl is the endpoint for oauth-authentication
     * @param clientID     client-id is the oauth server provided unique id for the user.
     * @param clientSecret client-secret is the user's password in the oauth server.
     * @param grantType    grant type describes the in which access token
     */

    public OAuthServerDetails(String serverUrl, String clientID, String clientSecret, String grantType) {
        this.serverUrl = serverUrl;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
