package com.example.pjaidmobile.data.model;

/**
 * A data model used for user authentication (login).
 * An object of this class is usually sent as the body of an HTTP request (e.g. POST) to the API server.
 */

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
