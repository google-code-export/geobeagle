package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import junit.framework.TestCase;

public class OnContentProviderSelectedListenerTest extends TestCase {

    public void testOnContentProviderSelectedListener() {
        MockableTextView contentProviderCaption = createMock(MockableTextView.class);
        MockableTextView gotoObjectCaption = createMock(MockableTextView.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        expect(resourceProvider.getStringArray(R.array.object_names)).andReturn(new String[] {
                "letterbox", "geocache"
        });
        contentProviderCaption.setText("Search for letterbox:");
        contentProviderCaption.setText("Search for geocache:");
        gotoObjectCaption.setText("Go to letterbox:");
        gotoObjectCaption.setText("Go to geocache:");

        replay(resourceProvider);
        replay(contentProviderCaption);
        replay(gotoObjectCaption);
        OnContentProviderSelectedListener ocpsl = new OnContentProviderSelectedListener(
                resourceProvider, contentProviderCaption, gotoObjectCaption);
        ocpsl.onItemSelected(null, null, 0, 0);
        ocpsl.onItemSelected(null, null, 1, 0);
        verify(resourceProvider);
        verify(contentProviderCaption);
        verify(gotoObjectCaption);
    }
}
