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

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CacheDetailsWriter {
    private final HtmlWriter mHtmlWriter;
    private String mLatitude;
    private String mLongitude;
    private int mLogNumber;
    private final Emotifier mEmotifier;
    private Context mContext;

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

    public static Date parse(String input) throws java.text.ParseException {
        // NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        // things a bit. Before we go on we have to repair this.
        String s = new String(input);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        // this is zero time so we need to add that TZ indicator for
        if (input.endsWith("Z")) {
            s = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            s = s0 + "GMT" + s1;
        }
        return df.parse(s);
    }

    public void writeLogDate(String text) throws IOException {
        mHtmlWriter.writeSeparator();
        try {
            mHtmlWriter.write(getRelativeTime(text));
        } catch (ParseException e) {
            mHtmlWriter.write("error parsing date: " + e.getLocalizedMessage());
        }
    }

    private String getRelativeTime(String text) throws ParseException {
        Date date = parse(text);
        final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(mContext,
                date.getTime(), DateUtils.HOUR_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        return relativeDateTimeString.toString();
    }

    public void writeWptName(String wpt) throws IOException {
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

    public void writeName(String name) throws IOException {
        mHtmlWriter.writeHeader();
        mHtmlWriter.write("<h3>" + name + "</h3>\n");
    }

    public void placedBy(String text, String time) throws IOException {
        Log.d("GeoBeagle", "PLACED BY: " + time);
        String on = "";
        try {
            on = "<font color=grey>at:</font> " + getRelativeTime(time);
        } catch (ParseException e) {
            on = "PARSE ERROR";
        }
        mHtmlWriter.write("<font color=grey>Placed by:</font> " + text
                + "<font color=grey>on:</font> " + on + "<br/>");
    }

    public void writeField(String fieldName, String field) throws IOException {
        mHtmlWriter.write("<font color=grey>" + fieldName + "</font>: " + field + "<br/>");
    }
}
