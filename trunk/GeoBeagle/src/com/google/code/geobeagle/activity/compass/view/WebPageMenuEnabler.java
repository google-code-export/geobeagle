package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory.Provider;

public class WebPageMenuEnabler {
    public boolean shouldEnable(Geocache geocache) {
        final Provider contentProvider = geocache.getContentProvider();
        return contentProvider == Provider.GROUNDSPEAK
                || contentProvider == Provider.ATLAS_QUEST
                || contentProvider == Provider.OPENCACHING;
    }
}