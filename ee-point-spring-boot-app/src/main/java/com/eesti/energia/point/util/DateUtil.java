package com.eesti.energia.point.util;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateUtil {

    public static final String UTC_TIME_ZONE = "UTC";
    private static final String format = "YYYY-MM-dd";

    public static DateTimeFormatter formatter = DateTimeFormat.forPattern(format)
            .withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE)));


    private DateUtil() {
    }

    public static Long getMillisFromString(String dateStr) {
        DateTime parsedDate = formatter.parseDateTime(dateStr);
        return parsedDate.getMillis();
    }

    public static String getStringFromMillis(Long millis) {
        DateTime parsedMillis = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE)));
        return parsedMillis.toString(formatter);
    }

}
