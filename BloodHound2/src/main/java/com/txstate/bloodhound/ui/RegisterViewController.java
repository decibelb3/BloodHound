package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.RegistrationRequest;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.service.AuthService;
import com.txstate.bloodhound.util.OperationResult;

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
     * @param request registration payload
     * @return operation result containing created user on success
     */
    public OperationResult<User> register(RegistrationRequest request) {
        OperationResult<User> result = authService.register(request);
        if (result.isSuccess()) {
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
