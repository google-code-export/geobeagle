
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

public class GotoCachePageTest extends TestCase {
    public void test() {
        Intent intent = createMock(Intent.class);
        ActivityStarter activityStarter = createMock(ActivityStarter.class);
        IntentFromActionUriFactory intentFromActionFactory = createMock(IntentFromActionUriFactory.class);
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);

        expect(resourceProvider.getString(R.string.cache_page_url)).andReturn(
                "http://coord.info/%1$s");
        expect(intentFromActionFactory.createIntent(Intent.ACTION_VIEW, "http://coord.info/GCFOO"))
                .andReturn(intent);
        activityStarter.startActivity(intent);

        replay(resourceProvider);
        replay(activityStarter);
        replay(intentFromActionFactory);
        new GotoCachePage(activityStarter, intentFromActionFactory, resourceProvider)
                .startIntent(new Destination("37 12.234 122 56.789 # GCFOO"));
        verify(resourceProvider);
        verify(activityStarter);
        verify(intentFromActionFactory);
    }
}
