package com.example.a1210733_1211088_courseproject.utils;

import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for password hashing and verification
 */
public class PasswordUtils {
    private static final String TAG = "PasswordUtils";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * Generates a salt for password hashing
     * @return A random salt as a hex string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }
    
    /**
     * Hashes a password with a given salt
     * @param password The plain text password
     * @param salt The salt to use for hashing
     * @return The hashed password as a hex string
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            
            // Combine password and salt
            String saltedPassword = password + salt;
            
            // Hash the salted password
            byte[] hash = md.digest(saltedPassword.getBytes());
            
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash to compare against
     * @param salt The salt used for the original hash
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedPassword = hashPassword(password, salt);
        return hashedPassword != null && hashedPassword.equals(storedHash);
    }
    
    /**
     * Simple password hashing without salt (for compatibility with existing data)
     * @param password The plain text password
     * @return The hashed password as a hex string
     */
    public static String hashPasswordSimple(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = md.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }
    
    /**
     * Converts byte array to hex string
     * @param bytes The byte array to convert
     * @return Hex string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
