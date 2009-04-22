
package com.google.code.geobeagle.ui.cachelist.row;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.IGeocacheVector;
import com.google.code.geobeagle.ui.cachelist.row.GeocacheSummaryRowInflater.RowViews;
import com.google.code.geobeagle.ui.cachelist.row.RowInflater.RowInfo;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

@PrepareForTest( {
        TextView.class, View.class, RowViews.class, RowInflater.class,
        GeocacheSummaryRowInflater.class
})
@RunWith(PowerMockRunner.class)
public class GeocacheSummaryRowInflaterTest {

    @Test
    public void testCacheRowViewsSet() {
        TextView txtCache = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);
        IGeocacheVector geocacheVector = PowerMock.createMock(IGeocacheVector.class);

        EasyMock.expect(geocacheVector.getIdAndName()).andReturn("GC123 my cache");
        EasyMock.expect(geocacheVector.getFormattedDistance()).andReturn("10m");
        txtCache.setText("GC123 my cache");
        txtDistance.setText("10m");

        PowerMock.replayAll();
        new GeocacheSummaryRowInflater.RowViews(txtCache, txtDistance).set(geocacheVector);
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateExisting() throws Exception {
        View convertView = PowerMock.createMock(View.class);
        RowInfo rowInfo = PowerMock.createMock(RowInfo.class);

        EasyMock.expect(convertView.getTag()).andReturn(rowInfo);
        EasyMock.expect(rowInfo.getType()).andReturn(RowType.CacheRow);

        PowerMock.replayAll();
        assertEquals(convertView, new GeocacheSummaryRowInflater(null, null).inflate(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateNew() throws Exception {
        View view = PowerMock.createMock(View.class);
        LayoutInflater layoutInflater = PowerMock.createMock(LayoutInflater.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        TextView txtView = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);

        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(view);
        EasyMock.expect(view.findViewById(R.id.txt_cache)).andReturn(txtView);
        EasyMock.expect(view.findViewById(R.id.distance)).andReturn(txtDistance);
        PowerMock.expectNew(GeocacheSummaryRowInflater.RowViews.class, txtView, txtDistance)
                .andReturn(rowViews);
        view.setTag(rowViews);

        PowerMock.replayAll();
        assertEquals(view, new GeocacheSummaryRowInflater(layoutInflater, null).inflate(null));
        PowerMock.verifyAll();
    }

    @Test
    public void testIsAlreadyInflated() {
        View convertView = PowerMock.createMock(View.class);
        RowInfo rowInfo = PowerMock.createMock(RowInfo.class);

        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                null, null);
        EasyMock.expect(convertView.getTag()).andReturn(rowInfo);
        EasyMock.expect(rowInfo.getType()).andReturn(RowType.CacheRow);

        PowerMock.replayAll();
        assertTrue(geocacheSummaryRowInflater.isAlreadyInflated(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testMatch() {
        assertFalse(new GeocacheSummaryRowInflater(null, null).match(0));
        assertTrue(new GeocacheSummaryRowInflater(null, null).match(1));
    }

    @Test
    public void testRowViewsGetType() {
        assertEquals(RowType.CacheRow, new RowViews(null, null).getType());
    }

    @Test
    public void testRowViewsSet() {
        IGeocacheVector geocacheVector = PowerMock.createMock(IGeocacheVector.class);
        TextView cache = PowerMock.createMock(TextView.class);
        TextView distance = PowerMock.createMock(TextView.class);

        EasyMock.expect(geocacheVector.getIdAndName()).andReturn("GC123 a cache");
        cache.setText("GC123 a cache");
        EasyMock.expect(geocacheVector.getFormattedDistance()).andReturn("27m");
        distance.setText("27m");

        PowerMock.replayAll();
        RowViews rowViews = new RowViews(cache, distance);
        rowViews.set(geocacheVector);
        PowerMock.verifyAll();

    }

    @Test
    public void testSetData() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        IGeocacheVector geocacheVector = PowerMock.createMock(IGeocacheVector.class);
        View view = PowerMock.createMock(View.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);

        EasyMock.expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        EasyMock.expect(view.getTag()).andReturn(rowViews);
        rowViews.set(geocacheVector);

        PowerMock.replayAll();
        new GeocacheSummaryRowInflater(null, geocacheVectors).setData(view, 18);
        PowerMock.verifyAll();
    }
}