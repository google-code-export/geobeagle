
package com.google.code.geobeagle.ui.cachelist;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.cachelist.GeocacheRowInflater;
import com.google.code.geobeagle.ui.cachelist.GeocacheRowInflater.RowViews;

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
        TextView.class, View.class, RowViews.class, GeocacheRowInflater.class
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
        RowViews rowViews = PowerMock.createMock(RowViews.class);
        TextView txtView = PowerMock.createMock(TextView.class);
        TextView txtDistance = PowerMock.createMock(TextView.class);

        EasyMock.expect(layoutInflater.inflate(R.layout.cache_row, null)).andReturn(convertView);
        EasyMock.expect(convertView.findViewById(R.id.txt_cache)).andReturn(txtView);
        EasyMock.expect(convertView.findViewById(R.id.distance)).andReturn(txtDistance);
        PowerMock.expectNew(RowViews.class, txtView, txtDistance).andReturn(rowViews);
        convertView.setTag(rowViews);

        PowerMock.replayAll();
        GeocacheRowInflater geocacheRowInflater = new GeocacheRowInflater(layoutInflater);
        assertEquals(convertView, geocacheRowInflater.inflateIfNecessary(null));
        PowerMock.verifyAll();
    }
}
