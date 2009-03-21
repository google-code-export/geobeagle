
package com.google.code.geobeagle;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.IGeocacheVector;
import com.google.code.geobeagle.ui.GeocacheRowInflater;
import com.google.code.geobeagle.ui.GeocacheListAdapter;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListAdapter.class, TextView.class
})
public class GeocacheListAdapterTest {
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
        new GeocacheRowInflater.GeocacheRowViews(txtCache, txtDistance).set(geocacheVector);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetCount() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        EasyMock.expect(geocacheVectors.size()).andReturn(34);

        PowerMock.replayAll();
        assertEquals(34, new GeocacheListAdapter(geocacheVectors, null).getCount());
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItem() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        PowerMock.replayAll();
        assertEquals(1763, new GeocacheListAdapter(geocacheVectors, null).getItem(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetItemId() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);

        PowerMock.replayAll();
        assertEquals(1763L, new GeocacheListAdapter(geocacheVectors, null).getItemId(1763));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetView() {
        PowerMock.suppressConstructor(BaseAdapter.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheRowInflater geocacheRowInflater = PowerMock.createMock(GeocacheRowInflater.class);
        IGeocacheVector geocacheVector = PowerMock.createMock(IGeocacheVector.class);
        View convertView = PowerMock.createMock(View.class);
        View newConvertView = PowerMock.createMock(View.class);
        GeocacheRowInflater.GeocacheRowViews geocacheRowViews = PowerMock
                .createMock(GeocacheRowInflater.GeocacheRowViews.class);

        EasyMock.expect(geocacheRowInflater.inflateIfNecessary(convertView)).andReturn(
                newConvertView);
        EasyMock.expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        EasyMock.expect(newConvertView.getTag()).andReturn(geocacheRowViews);
        geocacheRowViews.set(geocacheVector);

        PowerMock.replayAll();
        assertEquals(newConvertView, new GeocacheListAdapter(geocacheVectors, geocacheRowInflater)
                .getView(17, convertView, null));
        PowerMock.verifyAll();
    }
}
