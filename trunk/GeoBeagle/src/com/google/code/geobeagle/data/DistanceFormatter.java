
package com.google.code.geobeagle.data;

public class DistanceFormatter {

    public CharSequence format(float distance) {
        if (distance == -1) {
            return "";
        }
        if (distance >= 1000) {
            return String.format("%1$1.2fkm", distance / 1000.0);
        }
        return String.format("%1$dm", (int)distance);
    }
}
