package com.homework4.workapi.validation;

public final class ValidationConstants {

    public static final int USERNAME_MAX_LENGTH = 10;

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;

    public static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";

    private ValidationConstants() {
    }
}