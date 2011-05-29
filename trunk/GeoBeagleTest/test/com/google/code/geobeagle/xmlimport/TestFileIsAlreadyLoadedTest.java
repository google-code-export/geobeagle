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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.database.GpxTableWriterGpxFiles;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Log.class, LocAlreadyLoadedChecker.class
})
public class TestFileIsAlreadyLoadedTest {

    private SimpleDateFormat simpleDateFormat;
    private GpxTableWriterGpxFiles gpxTableWriterGpxFiles;

    @Before
    public void setUp() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.d((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        gpxTableWriterGpxFiles = PowerMock.createMock(GpxTableWriterGpxFiles.class);
    }

    @Test
    public void testNotLoc() {

        PowerMock.replayAll();
        assertFalse(new LocAlreadyLoadedChecker(gpxTableWriterGpxFiles, simpleDateFormat)
                .isAlreadyLoaded("foo.gpx"));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocTrue() throws Exception {
        File file = PowerMock.createMock(File.class);
        PowerMock.expectNew(File.class, "/sdcard/download/foo.loc").andReturn(file);
        EasyMock.expect(file.getName()).andReturn("foo.loc");
        EasyMock.expect(file.lastModified()).andReturn(0L);
        EasyMock.expect(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.loc", "1970-01-01 00:00:00.000+0000"))
                .andReturn(true);

        PowerMock.replayAll();
        assertTrue(new LocAlreadyLoadedChecker(gpxTableWriterGpxFiles, simpleDateFormat)
                .isAlreadyLoaded("/sdcard/download/foo.loc"));
        PowerMock.verifyAll();
    }

    @Test
    public void testLocFalse() {
        EasyMock.expect(gpxTableWriterGpxFiles.isGpxAlreadyLoaded("foo.loc", "1970-01-01 00:00:00.000+0000"))
                .andReturn(false);

        PowerMock.replayAll();
        assertFalse(new LocAlreadyLoadedChecker(gpxTableWriterGpxFiles, simpleDateFormat)
                .isAlreadyLoaded("foo.loc"));
        PowerMock.verifyAll();
    }
}
