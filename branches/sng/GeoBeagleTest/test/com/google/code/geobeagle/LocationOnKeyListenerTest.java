
package com.google.code.geobeagle;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;

public class LocationOnKeyListenerTest extends TestCase {

    private LocationOnKeyListener locationOnKeyListener;
    private CachePageButtonEnabler mCachePageButtonEnabler;

    public void testLocationOnKeyListener() {
        mCachePageButtonEnabler = createMock(CachePageButtonEnabler.class);
        locationOnKeyListener = new LocationOnKeyListener(mCachePageButtonEnabler);

        mCachePageButtonEnabler.check();
        replay(mCachePageButtonEnabler);
        assertFalse(locationOnKeyListener.onKey(null, 0, null));
        verify(mCachePageButtonEnabler);
    }

}
