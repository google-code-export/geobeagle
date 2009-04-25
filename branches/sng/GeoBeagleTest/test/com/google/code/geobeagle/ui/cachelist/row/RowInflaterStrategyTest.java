
package com.google.code.geobeagle.ui.cachelist.row;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.ui.cachelist.row.GeocacheSummaryRowInflater.RowViews;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        TextView.class, View.class, RowViews.class, RowInflater.class,
        GeocacheSummaryRowInflater.class, Log.class
})
public class RowInflaterStrategyTest {

    @Test
    public void testInflaterStrategy() {
        RowInflater rowInflater = PowerMock.createMock(RowInflater.class);
        View convertView = PowerMock.createMock(View.class);
        View view = PowerMock.createMock(View.class);
        PowerMock.mockStatic(Log.class);

        EasyMock.expect(rowInflater.match(12)).andReturn(true);
        EasyMock.expect(rowInflater.inflate(convertView)).andReturn(view);
        EasyMock.expect(Log.v((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);
        rowInflater.setData(view, 12);

        PowerMock.replayAll();
        assertEquals(view, new RowInflaterStrategy(new RowInflater[] {
            rowInflater
        }).getView(12, convertView));
        PowerMock.verifyAll();
    }

    @Test
    public void testInflaterStrategyNoMatches() {
        RowInflater rowInflater = PowerMock.createMock(RowInflater.class);
        View convertView = PowerMock.createMock(View.class);
        PowerMock.mockStatic(Log.class);

        EasyMock.expect(rowInflater.match(12)).andReturn(false);
        EasyMock.expect(Log.v((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0);

        PowerMock.replayAll();
        assertEquals(convertView, new RowInflaterStrategy(new RowInflater[] {
            rowInflater
        }).getView(12, convertView));
        PowerMock.verifyAll();
    }

}
