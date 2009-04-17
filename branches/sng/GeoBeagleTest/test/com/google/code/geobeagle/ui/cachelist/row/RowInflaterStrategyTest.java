
package com.google.code.geobeagle.ui.cachelist.row;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.ui.cachelist.row.GeocacheSummaryRowInflater.RowViews;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.view.View;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        TextView.class, View.class, RowViews.class, RowInflater.class,
        GeocacheSummaryRowInflater.class
})
public class RowInflaterStrategyTest {

    @Test
    public void testInflaterStrategy() {
        RowInflater rowInflater = PowerMock.createMock(RowInflater.class);
        View convertView = PowerMock.createMock(View.class);
        View view = PowerMock.createMock(View.class);

        EasyMock.expect(rowInflater.match(12)).andReturn(true);
        EasyMock.expect(rowInflater.inflate(convertView)).andReturn(view);
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

        EasyMock.expect(rowInflater.match(12)).andReturn(false);

        PowerMock.replayAll();
        assertEquals(convertView, new RowInflaterStrategy(new RowInflater[] {
            rowInflater
        }).getView(12, convertView));
        PowerMock.verifyAll();
    }

}
