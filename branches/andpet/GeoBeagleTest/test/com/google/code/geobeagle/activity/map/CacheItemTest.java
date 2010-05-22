/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.DbFrontend;

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
    public void testCreateSelectedCacheItem() throws Exception {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeoPoint geoPoint = PowerMock.createMock(GeoPoint.class);
        Resources resources = PowerMock.createMock(Resources.class);
        Drawable iconMap = PowerMock.createMock(Drawable.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        CacheItem cacheItem = PowerMock.createMock(CacheItem.class);
        Drawable glow = PowerMock.createMock(Drawable.class);
        GraphicsGenerator graphicsGenerator = PowerMock.createMock(GraphicsGenerator.class);
        Drawable selected = PowerMock.createMock(Drawable.class);

        EasyMock.expect(geocache.getGeoPoint()).andReturn(geoPoint);
        EasyMock.expect(
                geocache.getIconMap(resources, graphicsGenerator, dbFrontend))
                .andReturn(iconMap);
        EasyMock.expect(resources.getDrawable(R.drawable.glow_40px)).andReturn(
                glow);
        EasyMock.expect(graphicsGenerator.superimpose(iconMap, glow))
                .andReturn(selected);
        PowerMock.expectNew(CacheItem.class, geoPoint, geocache).andReturn(
                cacheItem);
        cacheItem.setMarker(selected);
        
        PowerMock.replayAll();
        CacheItemFactory cacheItemFactory = new CacheItemFactory(resources,
                graphicsGenerator, dbFrontend);
        cacheItemFactory.setSelectedGeocache(geocache);
        cacheItemFactory.createCacheItem(geocache);
        PowerMock.verifyAll();
    }
    
    @Test
    public void testCreateCacheItem() throws Exception {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        GeoPoint geoPoint = PowerMock.createMock(GeoPoint.class);
        Resources resources = PowerMock.createMock(Resources.class);
        Drawable iconMap = PowerMock.createMock(Drawable.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);
        CacheItem cacheItem = PowerMock.createMock(CacheItem.class);

        EasyMock.expect(geocache.getGeoPoint()).andReturn(geoPoint);
        EasyMock.expect(geocache.getIconMap(resources, null, dbFrontend)).andReturn(iconMap);
        PowerMock.expectNew(CacheItem.class, geoPoint, geocache).andReturn(cacheItem);
        cacheItem.setMarker(iconMap);
        
        PowerMock.replayAll();
        new CacheItemFactory(resources, null, dbFrontend).createCacheItem(geocache);
        PowerMock.verifyAll();
    }
}
