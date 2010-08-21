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
import com.google.inject.Inject;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CacheDetailsWriter {
    private final HtmlWriter htmlWriter;
    private String latitude;
    private String longitude;
    private int logNumber;
    private final Emotifier emotifier;
    private final Context context;
    private String time;

    @Inject
    public CacheDetailsWriter(HtmlWriter htmlWriter, Emotifier emotifier, Context context) {
        this.htmlWriter = htmlWriter;
        this.emotifier = emotifier;
        this.context = context;
    }

    public void close() throws IOException {
        htmlWriter.writeFooter();
        htmlWriter.close();
    }

    public void latitudeLongitude(String latitude, String longitude) {
        this.latitude = (String)Util.formatDegreesAsDecimalDegreesString(Double.valueOf(latitude));
        this.longitude = (String)Util.formatDegreesAsDecimalDegreesString(Double
                .valueOf(longitude));
    }

    public static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public void writeHint(String text) throws IOException {
        htmlWriter
                .write("<a class=hint id=hint_link onclick=\"dht('hint_link');return false;\" href=#>"
                        + "Encrypt</a>");
        htmlWriter.write("<div id=hint_link_text>" + text + "</div>");
    }

    public void writeLine(String text) throws IOException {
        htmlWriter.writeln(text);
    }

    public static Date parse(String input) throws java.text.ParseException {
        final String formatString = "yyyy-MM-dd'T'HH:mm:ss Z";
        SimpleDateFormat df = new SimpleDateFormat(formatString);

        String s;
        try {
            s = input.substring(0, 19) + " +0000";
        } catch (Exception e) {
            throw new ParseException(null, 0);
        }
        return df.parse(s);
    }

    public void writeLogDate(String text) throws IOException {
        htmlWriter.writeSeparator();
        try {
            htmlWriter.writeln(getRelativeTime(text));
        } catch (ParseException e) {
            htmlWriter.writeln("error parsing date: " + e.getLocalizedMessage());
        }
    }

    private String getRelativeTime(String utcTime) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(parse(utcTime));
        long time = calendar.getTimeInMillis();

        String timeClause;
        // 0700 and 1900 are noon and midnight PDT. These are probably cases
        // where the cache/log doesn't have a real time, and Groundspeak just
        // rounded off. In this case, suppress showing the time.
        if ((calendar.get(Calendar.HOUR_OF_DAY) % 12) == 7 && calendar.get(Calendar.MINUTE) == 0) {
            timeClause = "";
        } else {
            timeClause = ", "
                    + DateUtils.formatDateRange(context, time, time, DateUtils.FORMAT_SHOW_TIME);
        }

        long now = System.currentTimeMillis();
        long duration = Math.abs(now - time);
        if (duration < DateUtils.WEEK_IN_MILLIS) {
            CharSequence relativeClause = DateUtils.getRelativeTimeSpanString(time, now,
                    DateUtils.HOUR_IN_MILLIS, 0);
            return (String)relativeClause + timeClause;
        }
        CharSequence dateClause = DateUtils.getRelativeTimeSpanString(context, time, false);
        return dateClause + timeClause;
    }

    public void writeWptName() throws IOException {
        htmlWriter.open(null);
        htmlWriter.writeHeader();
        writeField("Location", latitude + ", " + longitude);
        latitude = longitude = null;
    }

    public void writeLogText(String text, boolean encoded) throws IOException {
        String f;
        if (encoded)
            f = "<a class=hint id=log_%1$s onclick=\"dht('log_%1$s');return false;\" "
                    + "href=#>Encrypt</a><div id=log_%1$s_text>%2$s</div>";
        else
            f = "%2$s";

        htmlWriter.writeln(String.format(f, logNumber++, emotifier.emotify(text)));
    }

    public void logType(String trimmedText) throws IOException {
        final String text = Emotifier.ICON_PREFIX + "log_"
                + trimmedText.replace(' ', '_').replace('\'', '_') + Emotifier.ICON_SUFFIX + " "
                + trimmedText;
        htmlWriter.writeln(text);
    }

    public void writeName(String name) throws IOException {
        htmlWriter.write("<center><h3>" + name + "</h3></center>\n");
    }

    public void placedBy(String text) throws IOException {
        Log.d("GeoBeagle", "PLACED BY: " + time);
        String on = "";
        try {
            on = getRelativeTime(time);
        } catch (ParseException e) {
            on = "PARSE ERROR";
        }
        writeField("Placed by", text);
        writeField("Placed on", on);
    }

    public void writeField(String fieldName, String field) throws IOException {
        htmlWriter.writeln("<font color=grey>" + fieldName + ":</font> " + field);
    }

    public void wptTime(String time) {
        this.time = time;
    }

    public void writeShortDescription(String trimmedText) throws IOException {
        htmlWriter.writeSeparator();
        htmlWriter.writeln(trimmedText);
        htmlWriter.writeln("");
    }

    public void writeLongDescription(String trimmedText) throws IOException {
        htmlWriter.write(trimmedText);
        htmlWriter.writeSeparator();
    }
}
