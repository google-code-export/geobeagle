/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

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
