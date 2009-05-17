
package com.google.code.geobeagle.data;

public class DistanceFormatter {

    private static final String[] ARROWS = {
            "^", ">", "v", "<",
    };

    public CharSequence formatDistance(float distance) {
        if (distance == -1) {
            return "";
        }
        if (distance >= 1000) {
            return String.format("%1$1.2fkm", distance / 1000.0);
        }
        return String.format("%1$dm", (int)distance);
    }

    String formatBearing(float currentBearing) {
        int currentBearingInt = (((int)(currentBearing) + 45 + 720) % 360) / 90;
        return ARROWS[currentBearingInt];
    }
}
