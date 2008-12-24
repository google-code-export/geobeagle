
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

import junit.framework.TestCase;

public class IntentStarterRadarTest extends TestCase {

    public void testStartIntent() {
        Intent intent = createMock(Intent.class);
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);

        expect(intentFactory.createIntent("com.google.android.radar.SHOW_RADAR")).andReturn(intent);
        expect(intent.putExtra("latitude", 37.175f)).andReturn(intent);
        expect(intent.putExtra("longitude", 122.8375f)).andReturn(intent);
        activityStarter.startActivity(intent);

        replay(intent);
        replay(activityStarter);
        replay(intentFactory);
        new IntentStarterRadar().startIntent(activityStarter, intentFactory, new Destination(
                "37 10.500 122 50.250 # GCFOO"));
        verify(intent);
        verify(activityStarter);
        verify(intentFactory);
    }
}
