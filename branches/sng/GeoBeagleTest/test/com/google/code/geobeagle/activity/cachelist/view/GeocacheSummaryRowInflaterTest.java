
package com.google.code.geobeagle.activity.cachelist.view;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
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
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock.createMock(RelativeBearingFormatter.class);

        EasyMock.expect(geocacheVector.getIdAndName()).andReturn("GC123 my cache");
        EasyMock.expect(geocacheVector.getFormattedDistance(distanceFormatter, relativeBearingFormatter))
                .andReturn("10m");
        txtCache.setText("GC123 my cache");
        txtDistance.setText("10m");

        PowerMock.replayAll();
        new GeocacheSummaryRowInflater.RowViews(txtCache, txtDistance).set(geocacheVector,
                distanceFormatter, relativeBearingFormatter);
        PowerMock.verifyAll();
    }

    @Test
    public void testInflateExisting() throws Exception {
        View convertView = PowerMock.createMock(View.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock.createMock(RelativeBearingFormatter.class);

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
        BearingFormatter relativeBearingFormatter = PowerMock.createMock(RelativeBearingFormatter.class);
        PowerMock.mockStatic(Log.class);

        EasyMock.expect(Log.v((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);
        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(view);
        EasyMock.expect(view.findViewById(R.id.txt_cache)).andReturn(txtView);
        EasyMock.expect(view.findViewById(R.id.distance)).andReturn(txtDistance);
        PowerMock.expectNew(GeocacheSummaryRowInflater.RowViews.class, txtView, txtDistance)
                .andReturn(rowViews);
        view.setTag(rowViews);

        PowerMock.replayAll();
        assertEquals(view, new GeocacheSummaryRowInflater(layoutInflater, null, distanceFormatter,
                relativeBearingFormatter).inflate(null));
        PowerMock.verifyAll();
    }

    @Test
    public void testRowViewsSet() {
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);
        TextView cache = PowerMock.createMock(TextView.class);
        TextView distance = PowerMock.createMock(TextView.class);
        DistanceFormatter distanceFormatter = PowerMock.createMock(DistanceFormatter.class);
        BearingFormatter relativeBearingFormatter = PowerMock.createMock(RelativeBearingFormatter.class);

        EasyMock.expect(geocacheVector.getIdAndName()).andReturn("GC123 a cache");
        cache.setText("GC123 a cache");
        EasyMock.expect(geocacheVector.getFormattedDistance(distanceFormatter, relativeBearingFormatter))
                .andReturn("27m");
        distance.setText("27m");

        PowerMock.replayAll();
        RowViews rowViews = new RowViews(cache, distance);
        rowViews.set(geocacheVector, distanceFormatter, relativeBearingFormatter);
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
        BearingFormatter relativeBearingFormatter = PowerMock.createMock(RelativeBearingFormatter.class);

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
