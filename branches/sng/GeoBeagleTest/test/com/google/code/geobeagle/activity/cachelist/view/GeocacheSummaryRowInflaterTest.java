
package com.google.code.geobeagle.activity.cachelist.view;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.AbsoluteBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.activity.cachelist.presenter.RelativeBearingFormatter;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater.RowViews;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

    @Test
    public void testCacheRowViewsSet() {
        TextView txtCache = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);
        ImageView imageView = PowerMock.createMock(ImageView.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);

        EasyMock.expect(geocacheVector.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.TRADITIONAL);
        EasyMock.expect(geocacheVector.getIdAndName()).andReturn("GC123 my cache");
        EasyMock.expect(
                geocacheVector.getFormattedDistance(distanceFormatter, relativeBearingFormatter))
                .andReturn("10m");
        txtCache.setText("GC123 my cache");
        txtDistance.setText("10m");
        imageView.setImageResource(R.drawable.cache_tradi);

        PowerMock.replayAll();
        new GeocacheSummaryRowInflater.RowViews(imageView, txtCache, txtDistance).set(
                geocacheVector, distanceFormatter, relativeBearingFormatter);
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateExisting() throws Exception {
        View convertView = PowerMock.createMock(View.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);

        PowerMock.replayAll();
        assertEquals(convertView, new GeocacheSummaryRowInflater(null, null, distanceFormatter,
                relativeBearingFormatter).inflate(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateNew() throws Exception {
        View view = PowerMock.createMock(View.class);
        LayoutInflater layoutInflater = PowerMock.createMock(LayoutInflater.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        TextView txtView = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);
        PowerMock.mockStatic(Log.class);
        ImageView imageView = PowerMock.createMock(ImageView.class);

        EasyMock.expect(Log.v((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(view);
        EasyMock.expect(view.findViewById(R.id.txt_cache)).andReturn(txtView);
        EasyMock.expect(view.findViewById(R.id.distance)).andReturn(txtDistance);
        EasyMock.expect(view.findViewById(R.id.gc_row_icon)).andReturn(imageView);
        PowerMock.expectNew(GeocacheSummaryRowInflater.RowViews.class, imageView, txtView,
                txtDistance).andReturn(rowViews);
        view.setTag(rowViews);

        PowerMock.replayAll();
        assertEquals(view, new GeocacheSummaryRowInflater(layoutInflater, null, distanceFormatter,
                relativeBearingFormatter).inflate(null));
        PowerMock.verifyAll();
    }

    @Test
    public void testSetBearingFormatterAbsolute() throws Exception {
        AbsoluteBearingFormatter absoluteBearingFormatter = PowerMock
                .createMock(AbsoluteBearingFormatter.class);
        PowerMock.expectNew(AbsoluteBearingFormatter.class).andReturn(absoluteBearingFormatter);

        PowerMock.replayAll();
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                null, null, null, null);
        geocacheSummaryRowInflater.setBearingFormatter(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetBearingFormatterRelative() throws Exception {
        RelativeBearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);
        PowerMock.expectNew(RelativeBearingFormatter.class).andReturn(relativeBearingFormatter);

        PowerMock.replayAll();
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                null, null, null, null);
        geocacheSummaryRowInflater.setBearingFormatter(false);
        assertEquals(relativeBearingFormatter, geocacheSummaryRowInflater.getBearingFormatter());
        PowerMock.verifyAll();
    }

    @Test
    public void testSetData() {
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        View view = PowerMock.createMock(View.class);
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        DistanceFormatter distanceFormatter2 = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock
                .createMock(RelativeBearingFormatter.class);

        EasyMock.expect(geocacheVectors.get(18)).andReturn(geocacheVector);
        EasyMock.expect(view.getTag()).andReturn(rowViews);
        rowViews.set(geocacheVector, distanceFormatter2, relativeBearingFormatter);

        PowerMock.replayAll();
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                null, geocacheVectors, distanceFormatter, relativeBearingFormatter);
        geocacheSummaryRowInflater.setDistanceFormatter(distanceFormatter2);
        geocacheSummaryRowInflater.setData(view, 18);
        PowerMock.verifyAll();
    }
}
