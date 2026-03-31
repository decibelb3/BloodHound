package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.User;

import java.time.LocalDateTime;

/**
 * Holds simple UI session state for the JavaFX shell.
 */
public class AppState {
    private User currentUser;
    private LocalDateTime filterStartDateTime;
    private LocalDateTime filterEndDateTime;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public LocalDateTime getFilterStartDateTime() {
        return filterStartDateTime;
    }

    public LocalDateTime getFilterEndDateTime() {
        return filterEndDateTime;
    }

    public void setDateTimeFilter(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        this.filterStartDateTime = startInclusive;
        this.filterEndDateTime = endInclusive;
    }

    public void clearDateTimeFilter() {
        this.filterStartDateTime = null;
        this.filterEndDateTime = null;
    }

    public boolean hasDateTimeFilter() {
        return filterStartDateTime != null && filterEndDateTime != null;
    }
}
