package com.google.code.geobeagle.database;

import com.google.code.geobeagle.Geocache;

import java.util.AbstractList;

public interface ICachesProviderArea extends ICachesProvider {

    void setBounds(double latLow, double lonLow, double latHigh, double lonHigh);

    /** @param maxResults If bigger than zero, the result may be limited to 
     * this number if there is a performance gain. */
    AbstractList<Geocache> getCaches(int maxResults);

    /** @return The number of caches returned if there aren't any bounds. */
    int getTotalCount();

    void clearBounds();
}
