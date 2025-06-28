package com.example.pjaidmobile.data.remote.api.auth;

import android.content.Context;

/**
 * Interface that defines the contract for the user logout mechanism.
 * Can be implemented by different classes (e.g. session management, token management, etc.).
 */

public interface LogoutHandler {
    void logout(Context context);
}
