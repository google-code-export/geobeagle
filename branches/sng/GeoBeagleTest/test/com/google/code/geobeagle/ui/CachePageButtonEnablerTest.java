
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.CachePageButtonEnabler;
import com.google.code.geobeagle.ui.TooString;

import android.view.View;

import junit.framework.TestCase;

public class CachePageButtonEnablerTest extends TestCase {

    public void helper(String input, boolean expected) {
        TooString tooString = createMock(TooString.class);
        View view = createMock(View.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);

        expect(resourceProvider.getStringArray(R.array.content_prefixes)).andReturn(new String[] {
                "GC", "LB"
        });
        expect(tooString.tooString()).andReturn(input);
        view.setEnabled(expected);

        replay(tooString);
        replay(resourceProvider);
        replay(view);
        CachePageButtonEnabler cachePageButtonEnabler = new CachePageButtonEnabler(tooString, view,
                resourceProvider);
        cachePageButtonEnabler.check();
        verify(tooString);
        verify(view);
        verify(resourceProvider);
    }

    public void testValid() {
        helper("12345 (GCxxx)", true);
        helper("12345 (LBxxx)", true);
    }

    public void testInvalid() {
        helper("12345 (CGxxx)", false);
    }
}
