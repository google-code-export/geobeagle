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
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.CacheDetailsHtmlWriter;
import com.google.code.geobeagle.cachedetails.Emotifier;
import com.google.code.geobeagle.cachedetails.HtmlWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Pattern;

@PrepareForTest({
        CacheDetailsHtmlWriter.class, Log.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsWriterTest extends GeoBeagleTest {

    @Test
    public void testWriteEndTag() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.writeFooter();
        htmlWriter.close();

        replayAll();
        new CacheDetailsHtmlWriter(htmlWriter, null, null, null).close();
        verifyAll();
    }

    @Test
    public void testWriteLine() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeln("some text");

        replayAll();
        new CacheDetailsHtmlWriter(htmlWriter, null, null, null).writeLine("some text");
        verifyAll();
    }

    @Test
    public void testWriteLogTextSmiley() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        EmotifierPatternProvider patternProvider = createMock(EmotifierPatternProvider.class);
        Context context = createMock(Context.class);

        Pattern pattern = EmotifierPatternProvider.createEmotifierPattern(new String[] {
                ":(", ":o)", "|)", "?"
        });
        expect(patternProvider.get()).andReturn(pattern).anyTimes();
        htmlWriter.writeln("<b>null null by null</b>");
        htmlWriter.writeln("<b>null null by null</b>");
        htmlWriter.writeln("<b>null null by null</b>");

        htmlWriter.writeln("sad " + Emotifier.EMOTICON_PREFIX + "3A28" + Emotifier.ICON_SUFFIX
                + " face");
        htmlWriter.writeln("clown " + Emotifier.EMOTICON_PREFIX + "3Ao29" + Emotifier.ICON_SUFFIX
                + " face");
        htmlWriter.writeln("not a smiley []");

        replayAll();
        CacheDetailsHtmlWriter cacheDetailsHtmlWriter = new CacheDetailsHtmlWriter(htmlWriter,
                new Emotifier(
                patternProvider), context, null);
        cacheDetailsHtmlWriter.writeLogText("sad [:(] face", false);
        cacheDetailsHtmlWriter.writeLogText("clown [:o)] face", false);
        cacheDetailsHtmlWriter.writeLogText("not a smiley []", false);
        verifyAll();
    }

    @Test
    public void testWriteWptName() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.open();
        htmlWriter.writeHeader();
        htmlWriter.writeln("<font color=grey>Location:</font> 37 00.000, 122 00.000");

        replayAll();
        CacheDetailsHtmlWriter cacheDetailsWriter = new CacheDetailsHtmlWriter(htmlWriter, null,
                null,
                null);
        cacheDetailsWriter.latitudeLongitude("37.0", "122.0");
        cacheDetailsWriter.writeWptName("GC123");
        verifyAll();
    }
}
