
package com.google.code.geobeagle.activity.map;

import static org.junit.Assert.assertEquals;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        OverlayItem.class, CacheItem.class
})
public class CacheItemTest {
    @Test
    public void testCreateCacheItem() {
        CacheDrawables cacheDrawables = PowerMock.createMock(CacheDrawables.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeoPoint geoPoint = PowerMock.createMock(GeoPoint.class);
        Drawable drawable = PowerMock.createMock(Drawable.class);

        EasyMock.expect(geocache.getGeoPoint()).andReturn(geoPoint);
        EasyMock.expect(geocache.getId()).andReturn("GC123");
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.MULTI);
        EasyMock.expect(cacheDrawables.get(CacheType.MULTI)).andReturn(drawable);

        PowerMock.suppressConstructor(CacheItem.class);
        PowerMock.suppressMethod(CacheItem.class, "setMarker");

        PowerMock.replayAll();
        new CacheItemFactory(cacheDrawables).createCacheItem(geocache);
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheDrawables() {
        Resources resources = PowerMock.createMock(Resources.class);
        Drawable drawable = PowerMock.createMock(Drawable.class);

        EasyMock.expect(resources.getDrawable(EasyMock.anyInt())).andReturn(drawable).anyTimes();
        EasyMock.expect(drawable.getIntrinsicWidth()).andReturn(18).anyTimes();
        EasyMock.expect(drawable.getIntrinsicHeight()).andReturn(22).anyTimes();
        drawable.setBounds(-9, -22, 9, 0);
        EasyMock.expectLastCall().anyTimes();

        PowerMock.replayAll();
        assertEquals(drawable, new CacheDrawables(resources).get(CacheType.MULTI));
        PowerMock.verifyAll();
    }
}
