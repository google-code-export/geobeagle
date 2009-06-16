
package com.google.code.geobeagle.activity.cachelist.presenter;

public class DistanceFormatterImperial implements DistanceFormatter {

    public CharSequence formatDistance(float distance) {
        if (distance == -1) {
            return "";
        }
        final float miles = distance / 1609.344f;
        if (miles > 0.05)
            return String.format("%1$1.2fmi", miles);
        else {
            final int yards = (int)(miles * (5280 / 3));
            return String.format("%1$1dyd", yards);
        }
    }
}
