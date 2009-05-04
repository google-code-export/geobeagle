
package com.google.code.geobeagle.io;

import android.location.Location;

public interface WhereFactory {

    public abstract String getWhere(Location location);

}
