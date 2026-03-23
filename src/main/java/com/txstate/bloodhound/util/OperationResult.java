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

    /**
     * Creates a successful result wrapper.
     *
     * @param message user-facing success message
     * @param data operation payload
     * @param <T> payload type
     * @return success result
     */
    public static <T> OperationResult<T> success(String message, T data) {
        return new OperationResult<>(true, message, List.of(), data);
    }

    /**
     * Creates a failed result wrapper.
     *
     * @param message user-facing failure message
     * @param errors detailed error messages
     * @param <T> payload type
     * @return failure result
     */
    public static <T> OperationResult<T> failure(String message, List<String> errors) {
        return new OperationResult<>(false, message, errors, null);
    }

    /**
     * Indicates whether the operation succeeded.
     *
     * @return {@code true} when successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the user-facing message.
     *
     * @return success or failure message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns an immutable list of error details.
     *
     * @return detailed error list (empty for successful operations)
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Returns operation payload.
     *
     * @return payload for successful operations, otherwise {@code null}
     */
    public T getData() {
        return data;
    }
}
