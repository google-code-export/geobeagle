
package com.google.code.geobeagle.activity.searchonline;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        JsInterfaceHelper.class, Uri.class
})

public class JsInterfaceTest {
    private LocationManager locationManager;
    private Location location;
    private JsInterfaceHelper helper;

    @Before
    public void setUp() {
        locationManager = PowerMock.createMock(LocationManager.class);
        location = PowerMock.createMock(Location.class);
        helper = PowerMock.createMock(JsInterfaceHelper.class);
    }

    @Test
    public void testLaunch() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Uri uri = PowerMock.createMock(Uri.class);
        PowerMock.mockStatic(Uri.class);

        EasyMock.expect(Uri.parse("http://foo")).andReturn(uri);
        PowerMock.expectNew(Intent.class, Intent.ACTION_VIEW, uri).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new JsInterfaceHelper(activity).launch("http://foo");
        PowerMock.verifyAll();
    }

    @Test
    public void testGetNSEW() {
        assertEquals("N", new JsInterfaceHelper(null).getNS(1));
        assertEquals("S", new JsInterfaceHelper(null).getNS(-1));
        assertEquals("E", new JsInterfaceHelper(null).getEW(1));
        assertEquals("W", new JsInterfaceHelper(null).getEW(-1));
    }

    @Test
    public void testGetTemplate() {
        Activity activity = PowerMock.createMock(Activity.class);
        Resources resources = PowerMock.createMock(Resources.class);

        EasyMock.expect(activity.getResources()).andReturn(resources);
        EasyMock.expect(resources.getStringArray(R.array.nearest_objects)).andReturn(new String[] {
                null, "http://foo?$1%d"
        });

        PowerMock.replayAll();
        assertEquals("http://foo?$1%d", new JsInterfaceHelper(activity).getTemplate(1));
        PowerMock.verifyAll();
    }

    @Test
    public void testAtlasQuestOrGroundspeak() {
        EasyMock.expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                .andReturn(location);
        EasyMock.expect(location.getLatitude()).andReturn(122.3);
        EasyMock.expect(location.getLongitude()).andReturn(37.8);

        EasyMock.expect(helper.getTemplate(3)).andReturn("%1$f, %2$f");
        helper.launch("122.300000, 37.800000");

        PowerMock.replayAll();
        assertEquals(0, new JsInterface(helper, null, locationManager).atlasQuestOrGroundspeak(3));
        PowerMock.verifyAll();
    }

    @Test
    public void testOpencaching() {
        EasyMock.expect(location.getLatitude()).andReturn(37.7773);
        EasyMock.expect(location.getLongitude()).andReturn(-122.1134);

        EasyMock.expect(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                .andReturn(location);
        EasyMock.expect(helper.getNS(37.7773)).andReturn("N");
        EasyMock.expect(helper.getEW(-122.1134)).andReturn("W");
        EasyMock.expect(helper.getTemplate(3)).andReturn("%1$s, %2$d, %3$.3f, %4$s, %5$d, %6$.3f");
        helper.launch("N, 37, 46.638, W, 122, 6.804");

        PowerMock.replayAll();
        assertEquals(0, new JsInterface(helper, null, locationManager).openCaching(3));
        PowerMock.verifyAll();
    }
}
