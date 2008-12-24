
package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Destination {
    private String mDescription;
    private double mLatitude;
    private double mLongitude;

    public Destination(CharSequence location) {
        mDescription = "";
        mLongitude = 0;
        mLatitude = 0;
        if (!extractLocationAndDescription(location))
            extractDescriptionOnly(location);
    }

    private void extractDescriptionOnly(CharSequence location) {
        final String REGEX = "[^#]*#?(.*)";
        Matcher matcher = Pattern.compile(REGEX).matcher(location);
        if (matcher.matches())
            mDescription = matcher.group(1).trim();
    }

    private boolean extractLocationAndDescription(CharSequence location) {
        final String REGEX = "\\s*(\\S+\\s+\\S+)\\s+(\\S+\\s+\\S+)\\s*#?(.*)";
        final Matcher matcher = Pattern.compile(REGEX).matcher(location);
        
        if (!matcher.matches())
            return false;
        try {
            mLatitude = Util.minutesToDegrees(matcher.group(1));
            mLongitude = Util.minutesToDegrees(matcher.group(2));
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        mDescription = matcher.group(3).trim();
        return true;
    }

    public final String getDescription() {
        return mDescription;
    }

    public final double getLatitude() {
        return mLatitude;
    }

    public final double getLongitude() {
        return mLongitude;
    }
}
