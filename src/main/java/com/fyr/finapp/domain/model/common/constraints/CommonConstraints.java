package com.fyr.finapp.domain.model.common.constraints;

import java.util.regex.Pattern;

public final class CommonConstraints {
    private CommonConstraints() {
    }

    public static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    public static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");
    public static final int ICON_MAX_LENGTH = 50;
}
