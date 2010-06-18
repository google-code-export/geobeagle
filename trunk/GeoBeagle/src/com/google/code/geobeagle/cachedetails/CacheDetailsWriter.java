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

package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.activity.main.Util;

import java.io.IOException;

public class CacheDetailsWriter {
    private final HtmlWriter mHtmlWriter;
    private String mLatitude;
    private String mLongitude;
    private int mLogNumber;
    private final Emotifier mEmotifier;

    public CacheDetailsWriter(HtmlWriter htmlWriter, Emotifier emotifier) {
        mHtmlWriter = htmlWriter;
        mEmotifier = emotifier;
    }

    public void close() throws IOException {
        mHtmlWriter.writeFooter();
        mHtmlWriter.close();
    }

    public void latitudeLongitude(String latitude, String longitude) {
        mLatitude = (String)Util.formatDegreesAsDecimalDegreesString(Double.valueOf(latitude));
        mLongitude = (String)Util.formatDegreesAsDecimalDegreesString(Double.valueOf(longitude));
    }

    public void open(String path) throws IOException {
        mHtmlWriter.open(path);
    }

    public static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public void writeHint(String text) throws IOException {
        mHtmlWriter
                .write("<a class=hint id=hint_link onclick=\"dht('hint_link');return false;\" href=#>"
                        + "Encrypt</a>");
        mHtmlWriter.write("<div id=hint_link_text>" + text + "</div>");
    }

    public void writeLine(String text) throws IOException {
        mHtmlWriter.write(text);
    }

    public void writeLogDate(String text) throws IOException {
        mHtmlWriter.writeSeparator();
        mHtmlWriter.write(text);
    }

    public void writeWptName(String wpt) throws IOException {
        mHtmlWriter.writeHeader();
        mHtmlWriter.write(wpt);
        mHtmlWriter.write(mLatitude + ", " + mLongitude);
        mLatitude = mLongitude = null;
    }

    public void writeLogText(String text, boolean encoded) throws IOException {
        String f;
        if (encoded)
            f = "<a class=hint id=log_%1$s onclick=\"dht('log_%1$s');return false;\" "
                    + "href=#>Encrypt</a><div id=log_%1$s_text>%2$s</div>";
        else
            f = "%2$s";

        mHtmlWriter.write(String.format(f, mLogNumber++, mEmotifier.emotify(text)));
    }

    public void logType(String trimmedText) throws IOException {
        mHtmlWriter.write(Emotifier.ICON_PREFIX + "log_" + trimmedText.replace(' ', '_')
                + Emotifier.ICON_SUFFIX);
    }
}
