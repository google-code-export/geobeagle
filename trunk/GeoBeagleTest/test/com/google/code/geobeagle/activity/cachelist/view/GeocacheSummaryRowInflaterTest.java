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

package com.google.code.geobeagle.activity.cachelist.view;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlay;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.ListViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@PrepareForTest( {
        TextView.class, View.class, RowViews.class, GeocacheSummaryRowInflater.class, Log.class
})
@RunWith(PowerMockRunner.class)
public class GeocacheSummaryRowInflaterTest {


    @Before
    public void allowLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void testCacheRowViewsSet() {
        TextView txtCacheName = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);
        TextView txtId = PowerMock.createMock(TextView.class);
        TextView txtAttributes = PowerMock.createMock(TextView.class);
        ImageView imageView = PowerMock.createMock(ImageView.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);
        Drawable drawable = PowerMock.createMock(Drawable.class);
        ListViewBitmapCopier listViewBitmapCopier = PowerMock.createMock(ListViewBitmapCopier.class);
        IconRenderer iconRenderer = PowerMock.createMock(IconRenderer.class);
        IconOverlay iconOverlay = PowerMock.createMock(IconOverlay.class);
        IconOverlayFactory iconOverlayFactory = PowerMock.createMock(IconOverlayFactory.class);
        NameFormatter nameFormatter = PowerMock.createMock(NameFormatter.class);

        nameFormatter.format(txtCacheName, true, false);
        EasyMock.expect(geocacheVector.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocache.getDifficulty()).andReturn(3);
        EasyMock.expect(geocache.getTerrain()).andReturn(7);
        EasyMock.expect(iconOverlayFactory.create(geocache, false)).andReturn(iconOverlay);
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.EARTHCACHE);
        EasyMock.expect(geocache.getAvailable()).andReturn(true);
        EasyMock.expect(geocache.getArchived()).andReturn(false);
        EasyMock.expect(geocacheVector.getName()).andReturn("my cache");
        EasyMock.expect(geocacheVector.getId()).andReturn("GC123");
        EasyMock.expect(geocacheVector.getFormattedAttributes()).andReturn("3.5 / 2.5");

        EasyMock.expect(
                geocacheVector.getFormattedDistance(distanceFormatter, relativeBearingFormatter))
                .andReturn("10m");
        txtId.setText("GC123");
        txtCacheName.setText("my cache");
        txtDistance.setText("10m");
        txtAttributes.setText("3.5 / 2.5");
        EasyMock.expect(
                iconRenderer.renderIcon(3, 7, R.drawable.cache_earth, iconOverlay,
                        listViewBitmapCopier)).andReturn(drawable);
        imageView.setImageDrawable(drawable);

        PowerMock.replayAll();
        new RowViews(txtAttributes, txtCacheName, txtDistance,
                imageView, txtId, iconOverlayFactory, nameFormatter).set(geocacheVector, distanceFormatter,
                relativeBearingFormatter, listViewBitmapCopier, iconRenderer);
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateExisting() throws Exception {
        View convertView = PowerMock.createMock(View.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);

        PowerMock.replayAll();
        assertEquals(convertView, new GeocacheSummaryRowInflater(distanceFormatter, null,
                relativeBearingFormatter, null, null, null, null).inflate(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateNew() throws Exception {
        View view = PowerMock.createMock(View.class);
        LayoutInflater layoutInflater = PowerMock.createMock(LayoutInflater.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        TextView txtCacheName = PowerMock.createMock(TextView.class);
        TextView txtAttributes = PowerMock.createMock(TextView.class);
        TextView txtCacheId = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);
        PowerMock.mockStatic(Log.class);
        ImageView imageView = PowerMock.createMock(ImageView.class);
        IconRenderer iconRenderer = PowerMock.createMock(IconRenderer.class);
        ListViewBitmapCopier listViewBitmapCopier = PowerMock.createMock(ListViewBitmapCopier.class);
        IconOverlayFactory iconOverlayFactory = PowerMock.createMock(IconOverlayFactory.class);
        NameFormatter nameFormatter = PowerMock.createMock(NameFormatter.class);

        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(view);
        EasyMock.expect(view.findViewById(R.id.txt_cache)).andReturn(txtCacheName);
        EasyMock.expect(view.findViewById(R.id.distance)).andReturn(txtDistance);
        EasyMock.expect(view.findViewById(R.id.gc_row_icon)).andReturn(imageView);
        EasyMock.expect(view.findViewById(R.id.txt_gcattributes)).andReturn(txtAttributes);
        EasyMock.expect(view.findViewById(R.id.txt_gcid)).andReturn(txtCacheId);
        PowerMock.expectNew(RowViews.class, txtAttributes, txtCacheName,
                txtDistance, imageView, txtCacheId, iconOverlayFactory, nameFormatter).andReturn(rowViews);
        view.setTag(rowViews);

        PowerMock.replayAll();
        assertEquals(view, new GeocacheSummaryRowInflater(distanceFormatter, layoutInflater,
                relativeBearingFormatter, iconRenderer, listViewBitmapCopier, iconOverlayFactory, nameFormatter)
                .inflate(null));
        PowerMock.verifyAll();
    }

    @Test
    public void testSetData() {
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        View view = PowerMock.createMock(View.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        DistanceFormatter distanceFormatter2 = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);
        ListViewBitmapCopier listViewBitmapCopier= PowerMock.createMock(ListViewBitmapCopier.class);
        IconRenderer iconRenderer = PowerMock.createMock(IconRenderer.class);

        EasyMock.expect(view.getTag()).andReturn(rowViews);
        rowViews.set(geocacheVector, distanceFormatter2, relativeBearingFormatter,
                listViewBitmapCopier, iconRenderer);

        PowerMock.replayAll();
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                distanceFormatter, null, relativeBearingFormatter, iconRenderer,
                listViewBitmapCopier, null, null);
        geocacheSummaryRowInflater.setDistanceFormatter(distanceFormatter2);
        geocacheSummaryRowInflater.setData(view, geocacheVector);
        PowerMock.verifyAll();
    }
}
