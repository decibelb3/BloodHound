package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.service.AuthService;

/**
 * Placeholder controller for user registration interactions.
 */
public class RegisterViewController {
    private final AuthService authService;
    private final AppState appState;

    public RegisterViewController(AuthService authService, AppState appState) {
        this.authService = authService;
        this.appState = appState;
    }

    /**
     * Registers a user account from registration form fields.
     *
     * @param username username
     * @param email email
     * @param plainTextPassword password
     * @return created user
     */
    public User register(String username, String email, String plainTextPassword) {
        // TODO: Validate registration fields and invoke AuthService.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AppState getAppState() {
        return appState;
    }
}
