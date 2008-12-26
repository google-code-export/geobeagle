
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import junit.framework.TestCase;

public class GotoCacheByViewUriTest extends TestCase {
    Destination destination = createMock(Destination.class);
    IntentFromActionUriFactory intentFromActionUriFactory = createMock(IntentFromActionUriFactory.class);

    public void testCreateIntent() {
        GotoCacheByViewingUri gotoCacheByViewUri = new GotoCacheByViewingUri(null,
                intentFromActionUriFactory) {

            String getUri(Destination d) {
                assertEquals(d, destination);
                return "a uri";
            }
        };
        Intent intent = createMock(Intent.class);
        expect(intentFromActionUriFactory.createIntent(Intent.ACTION_VIEW, "a uri")).andReturn(
                intent);

        replay(intentFromActionUriFactory);
        gotoCacheByViewUri.createIntent(destination);
        verify(intentFromActionUriFactory);
    }

}
