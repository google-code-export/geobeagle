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

package com.google.code.geobeagle;

import static org.junit.Assert.assertArrayEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.content.res.Resources;

@PrepareForTest( {
    Context.class
})
@RunWith(PowerMockRunner.class)
public class ResourceProviderTest {
    @Test
    public void getString() {
        Context context = PowerMock.createMock(Context.class);
        EasyMock.expect(context.getString(17)).andReturn("a resource");

        PowerMock.replayAll();
        ResourceProvider resourceProvider = new ResourceProvider(context);
        resourceProvider.getString(17);
        PowerMock.verifyAll();
    }

    @Test
    public void getStringArray() {
        Context context = PowerMock.createMock(Context.class);
        Resources resource = PowerMock.createMock(Resources.class);
        EasyMock.expect(context.getResources()).andReturn(resource);
        String[] strings = {
                "some", "strings"
        };
        EasyMock.expect(resource.getStringArray(17)).andReturn(strings);

        PowerMock.replayAll();
        ResourceProvider resourceProvider = new ResourceProvider(context);
        assertArrayEquals(strings, resourceProvider.getStringArray(17));
        PowerMock.verifyAll();
    }
}
