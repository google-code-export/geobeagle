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

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.cachedetails.Emotifier;
import com.google.code.geobeagle.cachedetails.HtmlWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import java.io.IOException;
import java.util.regex.Pattern;

@PrepareForTest( {
    CacheDetailsWriter.class, Log.class
})
@RunWith(PowerMockRunner.class)
public class CacheDetailsWriterTest extends GeoBeagleTest {

    @Test
    public void testWriteEndTag() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);

        htmlWriter.writeFooter();
        htmlWriter.close();

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null, null).close();
        verify(htmlWriter);
    }

    @Test
    public void testWriteLine() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeln("some text");

        replay(htmlWriter);
        new CacheDetailsWriter(htmlWriter, null, null).writeLine("some text");
        verify(htmlWriter);
    }

    @Test
    public void testWriteLogTextSmiley() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        htmlWriter.writeln("sad " + Emotifier.EMOTICON_PREFIX + "(" + Emotifier.ICON_SUFFIX + " face");
        htmlWriter.writeln("clown " + Emotifier.EMOTICON_PREFIX + "o)" + Emotifier.ICON_SUFFIX + " face");
        htmlWriter.writeln("not a smiley []");

        replay(htmlWriter);
        Pattern pattern = XmlimportModule.createEmotifierPattern(new String[] {
                ":(", ":o)", "|)", "?"
        });
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter, new Emotifier(
                pattern), null);
        cacheDetailsWriter.writeLogText("sad [:(] face", false);
        cacheDetailsWriter.writeLogText("clown [:o)] face", false);
        cacheDetailsWriter.writeLogText("not a smiley []", false);
        verify(htmlWriter);
    }

    @Test
    public void testWriteWptName() throws IOException {
        HtmlWriter htmlWriter = createMock(HtmlWriter.class);
        
        htmlWriter.open(null);
        htmlWriter.writeln("<font color=grey>Location:</font> 37 00.000, 122 00.000");

        replay(htmlWriter);
        CacheDetailsWriter cacheDetailsWriter = new CacheDetailsWriter(htmlWriter, null, null);
        cacheDetailsWriter.latitudeLongitude("37.0", "122.0");
        cacheDetailsWriter.writeWptName();
        verify(htmlWriter);
    }
}
