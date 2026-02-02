package com.fyr.finapp.adapters.driving.http.validation.messages;

public final class UserValidationMessages {
    private UserValidationMessages() {
    }

    public static final String NAME_REQUIRED = "Name is required";
    public static final String SURNAME_REQUIRED = "Surname is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Invalid email";
    public static final String EMAIL_TOO_LONG = "Email too long";
    public static final String PASSWORD_LENGTH = "Password must be between 10 and 72 characters";
    public static final String PASSWORD_REQUIRED = "Password is required";
}
