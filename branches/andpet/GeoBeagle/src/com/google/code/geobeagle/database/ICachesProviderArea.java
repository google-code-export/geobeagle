package com.google.code.geobeagle.database;

public interface ICachesProviderArea extends ICachesProvider {

    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh);
    
}
