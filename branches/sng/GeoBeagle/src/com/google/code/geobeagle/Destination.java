package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Destination {
    private static final Pattern PAT_EXTRACT_DESCRIPTION = Pattern.compile("[^#]*#?(.*)");
    private static final Pattern PAT_LOCATION_AND_DESCRIPTION = Pattern
            .compile("\\s*(\\S+\\s+\\S+)\\s+(\\S+\\s+\\S+)\\s*#?(.*)");
    
    public static CharSequence extractDescription(CharSequence location) {
        Matcher matcher = PAT_EXTRACT_DESCRIPTION.matcher(location);
        if (matcher.matches()) {
            final String afterPoundSign = matcher.group(1);
            if (afterPoundSign.length() > 0)
                return afterPoundSign.trim();
        }
        return location;
    }
    
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

    private boolean extractLocationAndDescription(CharSequence location) {
        final Matcher matcher = PAT_LOCATION_AND_DESCRIPTION.matcher(location);

        if (!matcher.matches())
            return false;
        try {
            mLatitude = Util.parseCoordinate(matcher.group(1));
            mLongitude = Util.parseCoordinate(matcher.group(2));
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
