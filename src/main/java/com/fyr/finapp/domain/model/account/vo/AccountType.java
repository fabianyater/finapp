package com.fyr.finapp.domain.model.account.vo;

import com.fyr.finapp.domain.exception.ValidationException;
import com.fyr.finapp.domain.model.account.exception.AccountErrorCode;
import com.fyr.finapp.domain.common.vo.Color;
import com.fyr.finapp.domain.common.vo.Icon;

public enum AccountType {
    CASH("Efectivo", Color.of("#f4b400"), Icon.of("money-bill")),
    BANK("Banco", Color.of("#007bff"), Icon.of("building")),
    CARD("Tarjeta", Color.of("#db4437"), Icon.of("credit-card"));

    private final String displayName;
    private final Color defaultColor;
    private final Icon defaultIcon;

    AccountType(String displayName, Color defaultColor, Icon defaultIcon) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.defaultIcon = defaultIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public Icon getDefaultIcon() {
        return defaultIcon;
    }

    public static AccountType fromString(String type) {
        try {
            return AccountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid account type: " + type + ". Valid types: CASH, BANK, CARD",
                    AccountErrorCode.NAME_TOO_LONG
            );
        }
    }
}
