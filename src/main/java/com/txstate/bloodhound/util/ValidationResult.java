package com.txstate.bloodhound.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of validating a health record.
 */
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String error) {
        if (error != null && !error.isBlank()) {
            errors.add(error);
        }
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
