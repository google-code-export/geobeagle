
package com.google.code.geobeagle.intents;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

import junit.framework.TestCase;

public class GotoCacheTest extends TestCase {

    public void testStartIntent() {
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        final Destination destination = createMock(Destination.class);
        final Intent intent = createMock(Intent.class);
        GotoCache gotoCache = new GotoCache(activityStarter) {

            @Override
            protected Intent createIntent(Destination d) {
                assertEquals(d, destination);
                return intent;
            }
        };
        
        activityStarter.startActivity(intent);
        
        replay(activityStarter);
        gotoCache.startIntent(destination);
        verify(activityStarter);
    }
}
