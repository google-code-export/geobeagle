
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.text.Editable;
import android.widget.Button;

import junit.framework.TestCase;

public class LocationOnKeyListenerTest extends TestCase {
    private Button btnCachePage;

    private Editable editable;

    private TooString tooString;

    private LocationOnKeyListener locationOnKeyListener;

    @Override
    public void setUp() {
        btnCachePage = createMock(Button.class);
        editable = createMock(Editable.class);
        tooString = createMock(TooString.class);
        locationOnKeyListener = new LocationOnKeyListener(btnCachePage, tooString);
    }

    public void testLocationOnKeyListener() {
        expect(tooString.tooString()).andReturn("37 03.0 122 00.0 # Description");
        btnCachePage.setEnabled(false);

        replayAndVerify();
    }

    private void replayAndVerify() {
        replay(tooString);
        replay(editable);
        replay(btnCachePage);
        assertFalse(locationOnKeyListener.onKey(null, 0, null));
        verify(editable);
        verify(btnCachePage);
        verify(tooString);
    }

    public void testLocationOnKeyListenerGC() {
        expect(tooString.tooString()).andReturn("37 03.0 122 00.0 # GC");
        btnCachePage.setEnabled(true);

        replayAndVerify();
    }

    public void testLocationOnKeyListenerNan() {
        expect(tooString.tooString()).andReturn("x37 03.0 122 00.0 # GC");
        btnCachePage.setEnabled(true);

        replayAndVerify();
    }

}
