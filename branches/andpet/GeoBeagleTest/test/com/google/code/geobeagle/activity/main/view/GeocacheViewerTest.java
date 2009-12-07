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
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.activity.main.RadarView;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        CacheType.class, TextView.class, UnlabelledAttributeViewer.class, 
        LabelledAttributeViewer.class, Geocache.class, Paint.class, GraphicsGenerator.class
})
public class GeocacheViewerTest {
    @Test
    public void testSetImageGone() {
        ImageView imageView = PowerMock.createMock(ImageView.class);
        imageView.setVisibility(View.GONE);

        PowerMock.replayAll();
        UnlabelledAttributeViewer unlabelledAttributeViewer = new UnlabelledAttributeViewer((int[])null,
                imageView);
        unlabelledAttributeViewer.setImage(0);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetImageLabelledAttributeViewer() throws Exception {
        ImageView imageView = PowerMock.createMock(ImageView.class);
        TextView label = PowerMock.createMock(TextView.class);
        UnlabelledAttributeViewer unlabelledAttributeViewer = PowerMock
                .createMock(UnlabelledAttributeViewer.class);
        Drawable drawable1 = PowerMock.createMock(Drawable.class);
        Drawable drawable2 = PowerMock.createMock(Drawable.class);
        Drawable drawable3 = PowerMock.createMock(Drawable.class);

        Drawable images[] = {
                drawable1, drawable2, drawable3
        };
        PowerMock.expectNew(UnlabelledAttributeViewer.class, images, imageView).andReturn(
                unlabelledAttributeViewer);

        unlabelledAttributeViewer.setImage(3);
        label.setVisibility(View.GONE);
        unlabelledAttributeViewer.setImage(0);
        label.setVisibility(View.VISIBLE);

        PowerMock.replayAll();
        LabelledAttributeViewer labelledAttributeViewer = new LabelledAttributeViewer(images,
                label, imageView);
        labelledAttributeViewer.setImage(3);
        labelledAttributeViewer.setImage(0);
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
        UnlabelledAttributeViewer unlabelledAttributeViewer = new UnlabelledAttributeViewer(images,
                imageView);
        unlabelledAttributeViewer.setImage(3);
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
        PowerMock.mockStatic(GraphicsGenerator.class);
        PowerMock.replay(GraphicsGenerator.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        RadarView radar = PowerMock.createMock(RadarView.class);
        UnlabelledAttributeViewer gcDifficulty = PowerMock
                .createMock(UnlabelledAttributeViewer.class);
        UnlabelledAttributeViewer gcContainer = PowerMock
                .createMock(UnlabelledAttributeViewer.class);
        UnlabelledAttributeViewer gcTerrain = PowerMock.createMock(UnlabelledAttributeViewer.class);
        CacheType cacheType = PowerMock.createMock(CacheType.class);
        ImageView gcTypeImageView = PowerMock.createMock(ImageView.class);

        expect(geocache.getLatitude()).andReturn(37.0);
        expect(geocache.getLongitude()).andReturn(-122.0);
        radar.setTarget(37000000, -122000000);
        expect(geocache.getId()).andReturn("GC123");
        expect(geocache.getName()).andReturn("a cache");
        expect(geocache.getCacheType()).andReturn(cacheType);
        expect(cacheType.iconBig()).andReturn(50);
        expect(geocache.getContainer()).andReturn(6);
        expect(geocache.getDifficulty()).andReturn(8);
        expect(geocache.getTerrain()).andReturn(5);
        gcContainer.setImage(6);
        gcDifficulty.setImage(8);
        gcTerrain.setImage(5);
        gcTypeImageView.setImageResource(50);

        id.setText("GC123");
        name.set("a cache");

        PowerMock.replayAll();
        new GeocacheViewer(radar, id, name, gcTypeImageView, gcDifficulty, 
                gcTerrain, gcContainer).set(geocache);
        PowerMock.verifyAll();
    }
}
