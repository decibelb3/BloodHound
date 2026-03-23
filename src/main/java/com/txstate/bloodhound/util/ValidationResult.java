package com.txstate.bloodhound.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of validating a health record.
 */
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    /**
     * Adds a validation error when non-blank.
     *
     * @param error validation message to add
     */
    public void addError(String error) {
        if (error != null && !error.isBlank()) {
            errors.add(error);
        }
    }

    /**
     * Indicates whether validation passed.
     *
     * @return {@code true} when no validation errors are present
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Returns an immutable list of validation errors.
     *
     * @return error list (possibly empty)
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
