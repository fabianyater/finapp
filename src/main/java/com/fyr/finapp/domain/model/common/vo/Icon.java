package com.fyr.finapp.domain.model.common.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.common.exception.SharedErrorCode;

import static com.fyr.finapp.domain.model.common.constraints.CommonConstraints.ICON_MAX_LENGTH;

public record Icon(String name) {
    public Icon {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Icon name cannot be null or empty", SharedErrorCode.ICON_NAME_REQUIRED);
        }

        String trimmed = name.trim();
        if (trimmed.length() > ICON_MAX_LENGTH) {
            throw new ValidationException(
                    "Icon name cannot exceed " + ICON_MAX_LENGTH + " characters",
                    SharedErrorCode.ICON_NAME_TOO_LONG
            );
        }

        name = trimmed;
    }

    public static Icon of(String name) {
        return new Icon(name);
    }

    public static Icon DEFAULT = new Icon("wallet");
}
