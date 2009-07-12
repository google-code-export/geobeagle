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

package com.google.code.geobeagle.activity.main.view;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.main.RadarView;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        CacheType.class, TextView.class
})
public class GeocacheViewerTest {
    @Test
    public void testSetImageGone() {
        ImageView imageView = PowerMock.createMock(ImageView.class);
        imageView.setVisibility(View.GONE);

        PowerMock.replayAll();
        AttributeViewer attributeViewer = new AttributeViewer(null, imageView);
        attributeViewer.setImage(0);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetImage() {
        ImageView imageView = PowerMock.createMock(ImageView.class);
        imageView.setImageResource(333);
        imageView.setVisibility(View.VISIBLE);

        PowerMock.replayAll();
        int images[] = {
                111, 222, 333
        };
        AttributeViewer attributeViewer = new AttributeViewer(images, imageView);
        attributeViewer.setImage(3);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetNameEmpty() {
        TextView textView = PowerMock.createMock(TextView.class);

        textView.setVisibility(View.GONE);
        PowerMock.replayAll();
        NameViewer nameViewer = new NameViewer(textView);
        nameViewer.set("");
        PowerMock.verifyAll();
    }

    @Test
    public void testSetName() {
        TextView textView = PowerMock.createMock(TextView.class);

        textView.setVisibility(View.VISIBLE);
        textView.setText("xyz");
        PowerMock.replayAll();
        NameViewer nameViewer = new NameViewer(textView);
        nameViewer.set("xyz");
        PowerMock.verifyAll();
    }

    @Test
    public void testSet() {
        TextView id = PowerMock.createMock(TextView.class);
        NameViewer name = PowerMock.createMock(NameViewer.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        RadarView radar = PowerMock.createMock(RadarView.class);
        AttributeViewer gcCacheType = PowerMock.createMock(AttributeViewer.class);
        AttributeViewer gcDifficulty = PowerMock.createMock(AttributeViewer.class);
        AttributeViewer gcContainer = PowerMock.createMock(AttributeViewer.class);
        AttributeViewer gcTerrain = PowerMock.createMock(AttributeViewer.class);
        CacheType cacheType = PowerMock.createMock(CacheType.class);

        expect(geocache.getLatitude()).andReturn(37.0);
        expect(geocache.getLongitude()).andReturn(-122.0);
        radar.setTarget(37000000, -122000000);
        expect(geocache.getId()).andReturn("GC123");
        expect(geocache.getName()).andReturn("a cache");
        expect(geocache.getCacheType()).andReturn(cacheType);
        expect(cacheType.toInt()).andReturn(12);
        expect(geocache.getContainer()).andReturn(6);
        expect(geocache.getDifficulty()).andReturn(8);
        expect(geocache.getTerrain()).andReturn(5);
        gcCacheType.setImage(12);
        gcContainer.setImage(6);
        gcDifficulty.setImage(8);
        gcTerrain.setImage(5);

        id.setText("GC123");
        name.set("a cache");

        PowerMock.replayAll();
        new GeocacheViewer(radar, id, name, gcCacheType, gcDifficulty, gcTerrain, gcContainer)
                .set(geocache);
        PowerMock.verifyAll();
    }
}
