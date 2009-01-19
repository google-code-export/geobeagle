package com.google.code.geobeagle.ui;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.widget.Spinner;

import junit.framework.TestCase;

public class ContentSelectorTest extends TestCase {

    public void testGetIndex() {
        Spinner spinner = createMock(Spinner.class);
        expect(spinner.getSelectedItemPosition()).andReturn(17);
        
        replay(spinner);
        ContentSelector contentSelector = new ContentSelector(spinner);
        assertEquals(17, contentSelector.getIndex());
        verify(spinner);
    }

}
