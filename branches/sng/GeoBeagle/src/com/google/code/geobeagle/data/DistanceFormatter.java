
package com.google.code.geobeagle.data;

public class DistanceFormatter {

    public CharSequence format(float distance) {
        if (distance == -1) {
            return "";
        }
        if (distance >= 1000) {
            return (int)(distance / 1000) + "km";
        }
        return (int)distance + "m";
    }
}
