package com.example.buggyapp.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

// DEFECT: Class name conflicts with common library names
public class StringUtils {

    // DEFECT: Utility class should have private constructor
    // DEFECT: Utility class not marked as final

    // DEFECT: Using MD5 for hashing (weak cryptographic algorithm)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // DEFECT: Swallowing exception and returning null
            return null;
        }
    }

    // DEFECT: Inefficient string concatenation
    public static String repeat(String str, int times) {
        String result = "";
        for (int i = 0; i < times; i++) {
            // DEFECT: String concatenation in loop
            result = result + str;
        }
        return result;
    }

    // DEFECT: No null check on input parameter
    public static boolean isEmpty(String str) {
        // DEFECT: Will throw NullPointerException if str is null
        return str.length() == 0;
    }

    // DEFECT: Duplicate functionality with isEmpty
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        // DEFECT: Inefficient - creates new object
        return str.trim().length() == 0;
    }

    // DEFECT: Weak random number generation for security purposes
    public static String generateToken() {
        Random random = new Random();

        // DEFECT: Predictable random number generation
        // DEFECT: Token too short for security
        return String.valueOf(random.nextInt(10000));
    }

    // DEFECT: Using default character encoding
    public static String encode(String input) {
        try {
            // DEFECT: Deprecated encoding method
            // DEFECT: Hardcoded encoding
            return java.net.URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // DEFECT: Returning original input on error (security issue)
            return input;
        }
    }

    // DEFECT: Method does too many things
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }

        // DEFECT: Inefficient multiple replace operations
        String result = input;
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("&", "&amp;");
        result = result.replace("\"", "&quot;");
        result = result.replace("'", "&#x27;");

        // DEFECT: Incomplete sanitization - many XSS vectors remain
        return result;
    }

    // DEFECT: Hardcoded cryptographic key
    private static final String SECRET_KEY = "ThisIsMySecretKey123";

    // DEFECT: Weak encryption implementation
    public static String simpleEncrypt(String text) {
        // DEFECT: XOR encryption is not secure
        StringBuilder encrypted = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            // DEFECT: Weak XOR encryption
            encrypted.append((char) (text.charAt(i) ^ SECRET_KEY.charAt(i % SECRET_KEY.length())));
        }

        return encrypted.toString();
    }

    // DEFECT: Method with complex boolean logic
    public static boolean isValidPassword(String password) {
        // DEFECT: No null check
        // DEFECT: Complex nested conditions
        if (password.length() >= 8) {
            if (password.matches(".*[A-Z].*")) {
                if (password.matches(".*[a-z].*")) {
                    if (password.matches(".*[0-9].*")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // DEFECT: Regex compiled on every invocation
    public static boolean isValidEmail(String email) {
        // DEFECT: Compiling regex pattern on every call (inefficient)
        // DEFECT: Incomplete email validation regex
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // DEFECT: Using == for string comparison
    public static boolean compareStrings(String str1, String str2) {
        // DEFECT: Using == instead of equals()
        return str1 == str2;
    }

    // DEFECT: Dead code - method never called
    private static void unusedMethod() {
        System.out.println("This method is never used");
    }

    // DEFECT: Magic numbers in code
    public static String truncate(String str, int maxLength) {
        if (str.length() > maxLength) {
            // DEFECT: Magic number 3 for ellipsis
            return str.substring(0, maxLength - 3) + "...";
        }
        return str;
    }

    // DEFECT: Method modifies static state
    private static int callCount = 0;

    public static String transform(String input) {
        // DEFECT: Modifying static variable without synchronization
        callCount++;

        // DEFECT: Side effect in transformation method
        System.out.println("Transform called " + callCount + " times");

        return input.toUpperCase();
    }
}
