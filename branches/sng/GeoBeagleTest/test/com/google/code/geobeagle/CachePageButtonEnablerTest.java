
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.view.View;

import junit.framework.TestCase;

public class CachePageButtonEnablerTest extends TestCase {

    public void helper(String input, boolean expected) {
        TooString tooString = createMock(TooString.class);
        View view = createMock(View.class);
        CachePageButtonEnabler cachePageButtonEnabler = new CachePageButtonEnabler(tooString, view);

        expect(tooString.tooString()).andReturn(input);
        view.setEnabled(expected);

        replay(tooString);
        replay(view);
        cachePageButtonEnabler.check();
        verify(tooString);
        verify(view);
    }

    public void testValid() {
        helper("12345 # GCxxx", true);
    }

    public void testInvalid() {
        helper("12345 # CGxxx", false);
    }
}
