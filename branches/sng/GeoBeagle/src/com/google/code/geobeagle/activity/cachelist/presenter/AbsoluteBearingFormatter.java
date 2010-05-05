
package com.google.code.geobeagle.activity.cachelist.presenter;

public class AbsoluteBearingFormatter implements BearingFormatter {
    private static final String[] LETTERS = {
            "N", "NE", "E", "SE", "S", "SW", "W", "NW"
    };

    public String formatBearing(float absBearing, float myHeading) {
        return LETTERS[((((int)(absBearing) + 22 + 720) % 360) / 45)];
    }
}
