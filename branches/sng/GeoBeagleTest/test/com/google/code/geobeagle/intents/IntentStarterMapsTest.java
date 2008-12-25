
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import android.content.Intent;

import junit.framework.TestCase;

public class IntentStarterMapsTest extends TestCase {

    public void testStartIntent() {
        Intent intent = createMock(Intent.class);
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);

        expect(resourceProvider.getString(R.string.map_intent)).andReturn(
                "geo:0,0?q=%1$.5f,%2$.5f (%3$s)");
        expect(intentFactory.createIntent(Intent.ACTION_VIEW, "geo:0,0?q=37.17500,122.83750 (GCFOO)"))
                .andReturn(intent);
        activityStarter.startActivity(intent);

        replay(resourceProvider);
        replay(activityStarter);
        replay(intentFactory);
        new GotoCacheMaps(resourceProvider).startIntent(activityStarter, intentFactory,
                new Destination("37 10.500 122 50.250 # GCFOO"));
        verify(resourceProvider);
        verify(activityStarter);
        verify(intentFactory);
    }

}
