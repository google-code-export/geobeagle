package com.google.code.geobeagle.test;

import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivity;

import android.test.ActivityInstrumentationTestCase2;

public class GeoBeagleTest extends ActivityInstrumentationTestCase2<SearchOnlineActivity> {

    public GeoBeagleTest() {
        super("com.google.code.geobeagle", SearchOnlineActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testCreate() {
        getActivity();
    }

}
