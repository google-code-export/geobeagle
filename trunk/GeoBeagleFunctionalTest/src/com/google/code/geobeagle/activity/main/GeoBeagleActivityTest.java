
package com.google.code.geobeagle.activity.main;

import android.test.ActivityInstrumentationTestCase2;

public class GeoBeagleActivityTest extends
        ActivityInstrumentationTestCase2<GeoBeagle> {

    public GeoBeagleActivityTest() {
        super("com.google.code.geobeagle", GeoBeagle.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreate() {
        final GeoBeagle geoBeagle = getActivity();
        assertNotNull(geoBeagle.locationControlBuffered);
        getInstrumentation().waitForIdleSync();
    }

    public void testFoo() {

    }
}
