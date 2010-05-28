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
    IntentFactory.class, Uri.class
})
public class IntentFactoryTest {
    
    @Test
    public void testCreateIntent() throws Exception {
        Uri uri = PowerMock.createMock(Uri.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.mockStatic(Uri.class);
        EasyMock.expect(Uri.parse("http://maps.google.com/etc")).andReturn(uri);
        
        PowerMock.expectNew(Intent.class, "action", uri).andReturn(intent);

        PowerMock.replayAll();
        assertEquals(intent, new IntentFactory().createIntent("action",
                "http://maps.google.com/etc"));
        PowerMock.verifyAll();
    }
}
