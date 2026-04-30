package com.fyr.finapp.domain.shared.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.shared.exception.SharedErrorCode;

import static com.fyr.finapp.domain.shared.constraints.CommonConstraints.HEX_PATTERN;

public record Color(String value) {

    public Color {
        if (value == null || !HEX_PATTERN.matcher(value).matches()) {
            throw new ValidationException(
                    "Invalid color format: " + value + ". Must be #RRGGBB (e.g., #004ab3)",
                    SharedErrorCode.INVALID_COLOR_FORMAT
            );
        }
    }

    public static Color of(String hex) {
        return new Color(hex);
    }

    public static final Color DEFAULT = new Color("#004ab3");
}
