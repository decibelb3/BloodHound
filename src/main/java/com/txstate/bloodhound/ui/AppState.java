package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.User;

/**
 * Holds simple UI session state for the JavaFX shell.
 */
public class AppState {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
