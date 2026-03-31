package com.txstate.bloodhound.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for password hashing and verification.
 * <p>
 * This implementation uses salted SHA-256 in a self-contained format:
 * {@code base64(salt):base64(hash)} where hash = SHA-256(salt + password).
 * For production hardening, migrate to Argon2id/PBKDF2/BCrypt.
 */
public final class PasswordUtil {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_BYTES = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * Generates a salted password hash for persistent storage.
     *
     * @param plainPassword raw password input
     * @return encoded salted hash in {@code salt:hash} form
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }

        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] hash = digest(salt, plainPassword);

        String saltEncoded = Base64.getEncoder().encodeToString(salt);
        String hashEncoded = Base64.getEncoder().encodeToString(hash);
        return saltEncoded + ":" + hashEncoded;
    }

    /**
     * Verifies a raw password against a stored salted hash.
     *
     * @param plainPassword raw password input
     * @param storedHash stored hash in {@code salt:hash} form
     * @return true when password matches
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || plainPassword.isBlank() || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;
        try {
            salt = Base64.getDecoder().decode(parts[0]);
            expectedHash = Base64.getDecoder().decode(parts[1]);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        byte[] computedHash = digest(salt, plainPassword);
        return MessageDigest.isEqual(expectedHash, computedHash);
    }

    private static byte[] digest(byte[] salt, String plainPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            messageDigest.update(salt);
            messageDigest.update(plainPassword.getBytes(StandardCharsets.UTF_8));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Hash algorithm unavailable: " + HASH_ALGORITHM, exception);
        }
    }
}
