
package com.google.code.geobeagle;

import android.net.UrlQuerySanitizer;
import android.net.UrlQuerySanitizer.ValueSanitizer;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String DATE_FORMAT_NOW = "HH:mm:ss";
    public static final String[] geocachingQueryParam = new String[] {
        "q"
    };

    private static String cleanCoordinate(String coord) {
        return coord.replace('+', ' ').trim();
    }

    public static String degreesToMinutes(double fDegrees) {
        final double fAbsDegrees = Math.abs(fDegrees);
        final int dAbsDegrees = (int)fAbsDegrees;
        final String format = "%1$d %2$06.3f";
        if (fDegrees < 0) {
            final String format2 = String.format((fDegrees < 0 ? "-" : "") + format, dAbsDegrees,
                    60.0 * (fAbsDegrees - dAbsDegrees));
            return format2;

        } else {
            return String.format("%1$d %2$06.3f", dAbsDegrees, 60.0 * (fAbsDegrees - dAbsDegrees));
        }
    }

    public static String[] getLatLonFromQuery(final String uri) {
        final String CACHE_AT_END = "([NS][^EW]*)([EW][^(]*)\\(([^)]*)\\).*";
        final String CACHE_AT_BEGINNING = "([^@])*@([NS][^EW]*)([EW][^(]*)";
        Matcher m = Pattern.compile(CACHE_AT_END).matcher(uri);
        if (m.matches()) {
            return new String[] {
                    cleanCoordinate(m.group(1)), cleanCoordinate(m.group(2)), m.group(3)
            };
        }
        m = Pattern.compile(CACHE_AT_BEGINNING).matcher(uri);
        m.matches();
        return new String[] {
                cleanCoordinate(m.group(2)), cleanCoordinate(m.group(3)), m.group(1)
        };
    }

    public static String getStackTrace(Exception e) {
        final StackTraceElement stack[] = e.getStackTrace();
        final StringBuilder sb = new StringBuilder();
        for (final StackTraceElement s : stack) {
            sb.append(s.toString() + "\n");
        }
        return sb.toString();
    }

    public static double minutesToDegrees(String string) {
        string = string.trim();
        int nsewSign = 1;
        if (string.substring(0, 1).matches("N|S|E|W")) {
            if (string.substring(0, 1).matches("S|W")) {
                nsewSign = -1;
            }
            string = string.substring(1);
        }
        string = string.trim();
        final String strings[] = string.split(" ");
        strings[0].replace("¡", " ");
        final String degreesTrimmed = strings[0].replace("¡", " ").trim();
        int degrees = Integer.parseInt(degreesTrimmed);
        if (degrees < 0 || degreesTrimmed.startsWith("-")) {
            degrees = -degrees;
            nsewSign *= -1;
        }
        double minutes = 0.0;
        if (strings.length > 1) {
            minutes = Double.parseDouble(strings[1]) / 60.0;
        }

        return nsewSign * (degrees + minutes);
    }

    public static String formatTime(long time) {
        return new SimpleDateFormat(DATE_FORMAT_NOW).format(time);
    }

    public static String parseHttpUri(String query, UrlQuerySanitizer sanitizer,
            ValueSanitizer valueSanitizer) {
        sanitizer.registerParameters(geocachingQueryParam, valueSanitizer);
        sanitizer.parseQuery(query);
        return sanitizer.getValue("q");
    }
}
