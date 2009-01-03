
package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Destination {
    private CharSequence mDescription;
    private double mLatitude;
    private double mLongitude;

    public Destination(CharSequence location) {
        mDescription = "";
        mLongitude = 0;
        mLatitude = 0;
        if (!extractLocationAndDescription(location))
            mDescription = extractDescription(location);
    }

    public static CharSequence extractDescription(CharSequence location) {
        final String REGEX = "[^#]*#?(.*)";
        Matcher matcher = Pattern.compile(REGEX).matcher(location);
        if (matcher.matches()) {
            final String afterPoundSign = matcher.group(1);
            if (afterPoundSign.length() > 0)
                return afterPoundSign.trim();
        }
        return location;
    }

    private boolean extractLocationAndDescription(CharSequence location) {
        final String REGEX = "\\s*(\\S+\\s+\\S+)\\s+(\\S+\\s+\\S+)\\s*#?(.*)";
        final Matcher matcher = Pattern.compile(REGEX).matcher(location);

        if (!matcher.matches())
            return false;
        try {
            mLatitude = Util.parseDecimalDegreesStringToDegrees(matcher.group(1));
            mLongitude = Util.parseDecimalDegreesStringToDegrees(matcher.group(2));
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        mDescription = matcher.group(3).trim();
        return true;
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
