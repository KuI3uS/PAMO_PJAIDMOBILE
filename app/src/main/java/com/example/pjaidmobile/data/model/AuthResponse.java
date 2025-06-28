package com.example.pjaidmobile.data.model;

/**
 * A data model used to receive responses from the server after a valid login.
 * Includes tokens: accessToken and refreshToken, which are used to authorize requests.
 */
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

