package com.fyr.finapp.domain.model.user;

import java.util.regex.Pattern;

public final class UserConstraints {
    private UserConstraints() {
    }

    public static final int DATE_FORMAT_LENGTH = 40;
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int HASHED_PASSWORD_MIN_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 10;
    public static final int PASSWORD_MAX_LENGTH = 72;

    public static final short FIRST_DAY_OF_WEEK_MIN = 1;
    public static final short FIRST_DAY_OF_WEEK_MAX = 7;

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,30}$");

}
