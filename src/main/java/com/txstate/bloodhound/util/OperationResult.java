package com.txstate.bloodhound.util;

import java.util.Collections;
import java.util.List;

/**
 * Generic wrapper for service operations.
 *
 * @param <T> payload type
 */
public class OperationResult<T> {
    private final boolean success;
    private final String message;
    private final List<String> errors;
    private final T data;

    private OperationResult(boolean success, String message, List<String> errors, T data) {
        this.success = success;
        this.message = message;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
        this.data = data;
    }

    public static <T> OperationResult<T> success(String message, T data) {
        return new OperationResult<>(true, message, List.of(), data);
    }

    public static <T> OperationResult<T> failure(String message, List<String> errors) {
        return new OperationResult<>(false, message, errors, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public T getData() {
        return data;
    }
}
