
package com.google.code.geobeagle.ui;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.GeocacheRowInflater.GeocacheRowViews;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        TextView.class, View.class, GeocacheRowViews.class, GeocacheRowInflater.class
})
public class GeocacheRowInflaterTest {
    @Test
    public void testGetExisting() {
        View convertView = PowerMock.createMock(View.class);

        PowerMock.replayAll();
        GeocacheRowInflater geocacheRowInflater = new GeocacheRowInflater(null);
        assertEquals(convertView, geocacheRowInflater.inflateIfNecessary(convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testGetCreateNew() throws Exception {
        View convertView = PowerMock.createMock(View.class);
        LayoutInflater layoutInflater = PowerMock.createMock(LayoutInflater.class);
        GeocacheRowViews geocacheRowViews = PowerMock.createMock(GeocacheRowViews.class);
        TextView txtView = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);

        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(convertView);
        EasyMock.expect(convertView.findViewById(R.id.txt_cache)).andReturn(txtView);
        EasyMock.expect(convertView.findViewById(R.id.distance)).andReturn(txtDistance);
        PowerMock.expectNew(GeocacheRowViews.class, txtView, txtDistance).andReturn(geocacheRowViews);
        convertView.setTag(geocacheRowViews);

        PowerMock.replayAll();
        GeocacheRowInflater geocacheRowInflater = new GeocacheRowInflater(layoutInflater);
        assertEquals(convertView, geocacheRowInflater.inflateIfNecessary(null));
        PowerMock.verifyAll();
    }
}
