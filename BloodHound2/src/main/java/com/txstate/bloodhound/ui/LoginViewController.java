package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.LoginRequest;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.service.AuthService;
import com.txstate.bloodhound.util.OperationResult;

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
     * @param usernameOrEmail username or email entered by user
     * @param rawPassword raw password entered by user
     * @return operation result containing the authenticated user on success
     */
    public OperationResult<User> onLogin(String usernameOrEmail, String rawPassword) {
        OperationResult<User> result = authService.login(new LoginRequest(usernameOrEmail, rawPassword));
        if (result.isSuccess() && result.getData() != null) {
            appState.setCurrentUser(result.getData());
        }
        return result;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AppState getAppState() {
        return appState;
    }
}
