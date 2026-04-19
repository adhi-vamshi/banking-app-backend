package com.bankingApp.utility;

import java.util.regex.Pattern;

public class PasswordValidator {
    // Password must be 8-16 characters with at least one uppercase, one number, and one special character
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$";

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }
}
