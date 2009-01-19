
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

    private static final Pattern PAT_COORD_COMPONENT = Pattern.compile("([\\d.]+)[^\\d]*");
    private static final Pattern PAT_LATLON = Pattern
            .compile("([NS]?[^NSEW,]*[NS]?),?\\s*([EW]?.*)");

    private static final Pattern PAT_NEGSIGN = Pattern.compile("[-WS]");
    private static final Pattern PAT_PAREN_FORMAT = Pattern.compile("([^(]*)\\(([^)]*).*");

    private static final Pattern PAT_SIGN = Pattern.compile("[-EWNS]");
    private static final Pattern PATTERN_ATSIGN_FORMAT = Pattern.compile("([^@]*)@(.*)");

    public static String formatDegreesAsDecimalDegreesString(double fDegrees) {
        final double fAbsDegrees = Math.abs(fDegrees);
        final int dAbsDegrees = (int)fAbsDegrees;
        return String.format((fDegrees < 0 ? "-" : "") + "%1$d %2$06.3f", dAbsDegrees,
                60.0 * (fAbsDegrees - dAbsDegrees));
    }

    public static String formatTime(long time) {
        return new SimpleDateFormat(DATE_FORMAT_NOW).format(time);
    }

    public static String[] getLatLonDescriptionFromQuery(String string) {
        String coordsAndDescription[] = splitCoordsAndDescription(string);
        String latLon[] = splitLatLon(coordsAndDescription[0]);

        return new String[] {
                latLon[0], latLon[1], coordsAndDescription[1]
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

    public static double parseCoordinate(String string) {
        int sign = 1;
        final Matcher negsignMatcher = PAT_NEGSIGN.matcher(string);
        if (negsignMatcher.find()) {
            sign = -1;
        }

        final Matcher signMatcher = PAT_SIGN.matcher(string);
        string = signMatcher.replaceAll("");

        final Matcher dmsMatcher = PAT_COORD_COMPONENT.matcher(string);
        double degrees = 0.0;
        for (double scale = 1.0; scale <= 3600.0 && dmsMatcher.find(); scale *= 60.0) {
            degrees += Double.parseDouble(dmsMatcher.group(1)) / scale;
        }
        return sign * degrees;
    }

    public static String parseHttpUri(String query, UrlQuerySanitizer sanitizer,
            ValueSanitizer valueSanitizer) {
        sanitizer.registerParameters(geocachingQueryParam, valueSanitizer);
        sanitizer.parseQuery(query);
        return sanitizer.getValue("q");
    }

    public static String[] splitCoordsAndDescription(String string) {
        Matcher matcher = PATTERN_ATSIGN_FORMAT.matcher(string);
        if (matcher.matches()) {
            return new String[] {
                    matcher.group(2), matcher.group(1)
            };
        }
        matcher = PAT_PAREN_FORMAT.matcher(string);
        if (matcher.matches()) {
            return new String[] {
                    matcher.group(1), matcher.group(2)
            };
        }
        return null;
    }

    public static String[] splitLatLon(String string) {
        Matcher matcher = PAT_LATLON.matcher(string);
        if (matcher.matches())
            return new String[] {
                    matcher.group(1).trim(), matcher.group(2).trim()
            };
        return null;
    }
}
