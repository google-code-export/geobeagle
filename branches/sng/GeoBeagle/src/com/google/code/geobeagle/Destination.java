
package com.google.code.geobeagle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cache or letterbox description, id, and coordinates.
 */
public class Destination {
    public static CharSequence extractDescription(CharSequence location) {
        return Util.splitCoordsAndDescription(location)[1];
    }

    static Pattern[] getDestinationPatterns(ResourceProvider resourceProvider) {
        return getDestinationPatterns(resourceProvider.getStringArray(R.array.content_prefixes));
    }

    static Pattern[] getDestinationPatterns(String contentPrefixes[]) {
        Pattern mContentSelectors[] = new Pattern[contentPrefixes.length];
        for (int ix = 0; ix < contentPrefixes.length; ix++) {
            mContentSelectors[ix] = Pattern.compile("(?:" + contentPrefixes[ix] + ")(\\w*)");
        }
        return mContentSelectors;
    }

    private int mContentSelectorIndex;
    private CharSequence mDescription;
    private CharSequence mId;
    private double mLatitude;
    private double mLongitude;

    public Destination(CharSequence location, Pattern destinationPatterns[]) {
        CharSequence latLonDescription[] = Util.splitLatLonDescription(location);
        try {
            mLatitude = Util.parseCoordinate(latLonDescription[0]);
            mLongitude = Util.parseCoordinate(latLonDescription[1]);
        } catch (NumberFormatException numberFormatException) {
        }
        mDescription = latLonDescription[2];

        for (mContentSelectorIndex = destinationPatterns.length - 1; mContentSelectorIndex >= 0; mContentSelectorIndex--) {
            Matcher matcher = destinationPatterns[mContentSelectorIndex].matcher(mDescription);
            if (matcher.find()) {
                mId = matcher.group(1);
                return;
            }
        }
    }

    public int getContentIndex() {
        return mContentSelectorIndex;
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public CharSequence getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
