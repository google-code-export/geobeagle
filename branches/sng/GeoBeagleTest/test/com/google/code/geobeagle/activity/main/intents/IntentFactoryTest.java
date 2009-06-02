
package com.google.code.geobeagle.activity.main.intents;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.activity.main.UriParser;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Intent;
import android.net.Uri;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    IntentFactory.class
})
public class IntentFactoryTest {
    @Test
    public void testCreateIntent() throws Exception {
        UriParser uriParser = PowerMock.createMock(UriParser.class);
        Uri uri = PowerMock.createMock(Uri.class);
        Intent intent = PowerMock.createMock(Intent.class);

        EasyMock.expect(uriParser.parse("http://maps.google.com/etc")).andReturn(uri);
        PowerMock.expectNew(Intent.class, "action", uri).andReturn(intent);

        PowerMock.replayAll();
        assertEquals(intent, new IntentFactory(uriParser).createIntent("action",
                "http://maps.google.com/etc"));
        PowerMock.verifyAll();
    }
}
