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

package com.google.code.geobeagle.xmlimport;

import static org.junit.Assert.*;

import com.google.code.geobeagle.database.CacheWriter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    Log.class
})
public class TestFileIsAlreadyLoadedTest {

    @Before
    public void allowLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void testNotLoc() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);

        PowerMock.replayAll();
        assertFalse(new FileAlreadyLoadedChecker(cacheWriter).isAlreadyLoaded("foo.gpx"));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocTrue() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);
        EasyMock.expect(cacheWriter.isGpxAlreadyLoaded("foo.loc", "1969-12-31 16:00:00"))
                .andReturn(true);

        PowerMock.replayAll();
        assertTrue(new FileAlreadyLoadedChecker(cacheWriter).isAlreadyLoaded("foo.loc"));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocFalse() {
        CacheWriter cacheWriter = PowerMock.createMock(CacheWriter.class);
        EasyMock.expect(cacheWriter.isGpxAlreadyLoaded("foo.loc", "1969-12-31 16:00:00"))
                .andReturn(false);

        PowerMock.replayAll();
        assertFalse(new FileAlreadyLoadedChecker(cacheWriter).isAlreadyLoaded("foo.loc"));
        PowerMock.verifyAll();
    }
}
