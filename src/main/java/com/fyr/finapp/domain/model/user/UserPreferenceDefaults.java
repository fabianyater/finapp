package com.fyr.finapp.domain.model.user;

import com.fyr.finapp.domain.model.user.vo.*;

public final class UserPreferenceDefaults {
    private UserPreferenceDefaults() {
    }

    public static final LocaleTag LOCALE = new LocaleTag("es-CO");
    public static final CurrencyCode CURRENCY = new CurrencyCode("COP");
    public static final TimezoneId TIMEZONE = new TimezoneId("America/Bogota");
    public static final Boolean DARK_MODE = false;
    public static final FirstDayOfWeek FIRST_DAY_OF_WEEK = new FirstDayOfWeek((short) 1);
    public static final DateFormatPattern DATE_FORMAT = new DateFormatPattern("dd/MM/yyyy");
}
