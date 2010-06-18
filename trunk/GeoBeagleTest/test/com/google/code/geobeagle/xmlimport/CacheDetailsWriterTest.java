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

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.Emotifier;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.HtmlWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

@PrepareForTest( {
    CacheDetailsWriter.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsWriterTest {
    @Test
    public void testOpen() throws Exception {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        FilePathStrategy filePathStrategy = createMock(FilePathStrategy.class);
        File filePath = createMock(File.class);
        String path = "/sdcard/details/oakland.gpx/GC123.html";
        String parent = "/sdcard/details/oakland.gpx";
        File fileParent = createMock(File.class);

        expect(filePathStrategy.getPath("oakland.gpx", "GC123", "html")).andReturn(path);
        expectNew(File.class, path).andReturn(filePath);
        htmlWriter.open(path);
        expect(filePath.getParent()).andReturn(parent);
        expectNew(File.class, parent).andReturn(fileParent);
        expect(fileParent.mkdirs()).andReturn(true);

        replayAll();
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter, null);
        cacheDetailsWriter.open("GC123");
        verifyAll();
    }

    @Test
    public void testWriteEndTag() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.writeFooter();
        htmlWriter.close();

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null).close();
        verify(htmlWriter);
    }

    @Test
    public void testWriteHint() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("<br />Hint: <font color=gray>a hint</font>");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null).writeHint("a hint");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLine() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("some text");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null).writeLine("some text");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLogDate() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeSeparator();
        htmlWriter.write("04/30/1963");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null).writeLogDate("04/30/1963");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLogTextSmiley() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.write("sad " + Emotifier.EMOTICON_PREFIX + "(" + Emotifier.ICON_SUFFIX + " face");
        htmlWriter.write("clown " + Emotifier.EMOTICON_PREFIX + "o)" + Emotifier.ICON_SUFFIX + " face");
        htmlWriter.write("not a smiley []");

        replay(htmlWriter);
        Pattern pattern = XmlimportModule.createEmotifierPattern(new String[] {
                ":(", ":o)", "|)", "?"
        });
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter, new Emotifier(
                pattern));
        cacheDetailsWriter.writeLogText("sad [:(] face", false);
        cacheDetailsWriter.writeLogText("clown [:o)] face", false);
        cacheDetailsWriter.writeLogText("not a smiley []", false);
        verify(htmlWriter);
    }

    @Test
    public void testWriteWptName() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeHeader();
        htmlWriter.write("GC1234");
        htmlWriter.write("37 00.000, 122 00.000");

        replay(htmlWriter);
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter, null);
        cacheDetailsWriter.latitudeLongitude("37.0", "122.0");
        cacheDetailsWriter.writeWptName("GC1234");
        verify(htmlWriter);
    }
}
