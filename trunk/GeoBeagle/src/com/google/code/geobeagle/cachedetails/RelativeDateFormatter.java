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

import com.google.inject.Inject;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class RelativeDateFormatter {
    private final Calendar gmtCalendar;

    @Inject
    RelativeDateFormatter() {
        this.gmtCalendar = Calendar.getInstance();
    }

    String getRelativeTime(Context context, String utcTime) throws ParseException {
        long now = System.currentTimeMillis();
        gmtCalendar.setTime(parse(utcTime));
        long timeInMillis = gmtCalendar.getTimeInMillis();

        long duration = Math.abs(now - timeInMillis);
        if (duration < DateUtils.WEEK_IN_MILLIS) {
            return (String)DateUtils.getRelativeTimeSpanString(timeInMillis, now,
                    DateUtils.DAY_IN_MILLIS, 0);
        }
        return (String)DateUtils.getRelativeTimeSpanString(context, timeInMillis, false);
    }

    private Date parse(String input) throws java.text.ParseException {
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
}
