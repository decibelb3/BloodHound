package com.txstate.bloodhound.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic operation result wrapper for service-layer success/failure responses.
 *
 * @param <T> payload type
 */
public class OperationResult<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final List<String> errors;

    private OperationResult(boolean success, String message, T data, List<String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors == null ? List.of() : new ArrayList<>(errors);
    }

    /**
     * Builds a successful result response.
     *
     * @param message user-facing message
     * @param data payload
     * @param <T> payload type
     * @return success result
     */
    public static <T> OperationResult<T> success(String message, T data) {
        return new OperationResult<>(true, message, data, List.of());
    }

    /**
     * Builds a failed result response.
     *
     * @param message user-facing message
     * @param errors validation or processing errors
     * @param <T> payload type
     * @return failure result
     */
    public static <T> OperationResult<T> failure(String message, List<String> errors) {
        return new OperationResult<>(false, message, null, errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
