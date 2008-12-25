
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.content.Intent;
import android.location.Location;

import junit.framework.TestCase;

public class SelectCacheFromNearestCachesTest extends TestCase {

    public void testStartIntent() {
        GetCoordsToast getCoordsToast = createMock(GetCoordsToast.class);
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        Intent intent = createMock(Intent.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        MyLocationProvider myLocationProvider = createMock(MyLocationProvider.class);
        Location location = createMock(Location.class);

        expect(resourceProvider.getString(R.string.nearest_caches_url)).andReturn(
                "http://www.geocaching.com/nearest?slat=37.00000&lng=122.00000");
        getCoordsToast.show();
        expect(
                intentFactory.createIntent(Intent.ACTION_VIEW,
                        "http://www.geocaching.com/nearest?slat=37.00000&lng=122.00000"))
                .andReturn(intent);
        activityStarter.startActivity(intent);
        expect(myLocationProvider.getLocation()).andReturn(location);
        expect(location.getLatitude()).andReturn(37.0);
        expect(location.getLongitude()).andReturn(122.0);

        replay(location);
        replay(myLocationProvider);
        replay(resourceProvider);
        replay(getCoordsToast);
        replay(intent);
        new SelectCacheFromNearestCaches(getCoordsToast, resourceProvider, myLocationProvider).startIntent(
                activityStarter, intentFactory);
        verify(resourceProvider);
        verify(getCoordsToast);
        verify(intent);
        verify(location);
        verify(myLocationProvider);
    }

}
