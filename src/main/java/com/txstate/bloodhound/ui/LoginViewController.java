package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.service.AuthService;

/**
 * Placeholder JavaFX controller for login interactions.
 */
public class LoginViewController {
    private final AuthService authService;
    private final AppState appState;

    public LoginViewController(AuthService authService, AppState appState) {
        this.authService = authService;
        this.appState = appState;
    }

    /**
     * Handles a login action from UI controls.
     *
     * @param username username entered by user
     * @param rawPassword raw password entered by user
     */
    public void onLogin(String username, String rawPassword) {
        // TODO: Wire login controls to AuthService and update AppState.
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AppState getAppState() {
        return appState;
    }
}
