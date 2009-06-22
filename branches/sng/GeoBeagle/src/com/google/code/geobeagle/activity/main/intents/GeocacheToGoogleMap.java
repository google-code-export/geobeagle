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

package com.google.code.geobeagle.activity.main.intents;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import java.util.Locale;

public class GeocacheToGoogleMap implements GeocacheToUri {
    public static String stringToHTMLString(CharSequence charSequence) {
        StringBuffer sb = new StringBuffer(charSequence.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = charSequence.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = charSequence.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("&lt;br/&gt;");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160)
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }

    private final ResourceProvider mResourceProvider;

    public GeocacheToGoogleMap(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public String convert(Geocache geocache) {
        // "geo:%1$.5f,%2$.5f?name=cachename"
        final CharSequence idAndName = stringToHTMLString(geocache.getIdAndName());
        return String.format(Locale.US, mResourceProvider.getString(R.string.map_intent), geocache
                .getLatitude(), geocache.getLongitude(), idAndName);
    }
}
