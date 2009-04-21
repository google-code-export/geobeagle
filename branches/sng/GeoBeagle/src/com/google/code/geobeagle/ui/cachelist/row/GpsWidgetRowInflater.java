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

package com.google.code.geobeagle.ui.cachelist.row;

import android.view.View;

/**
 * @author sng
 */
public class GpsWidgetRowInflater implements RowInflater {
    public static class GpsWidgetRowViews implements RowInfo {

        public RowType getType() {
            return RowType.GpsWidgetRow;
        }
    }

    private final View mGpsWidgetRowView;

    GpsWidgetRowInflater(View gpsWidgetRowView) {
        mGpsWidgetRowView = gpsWidgetRowView;
    }

    public View inflate(View convertView) {
        if (isAlreadyInflated(convertView))
            return convertView;

        return mGpsWidgetRowView;
    }

    public boolean isAlreadyInflated(View convertView) {
        return convertView != null && convertView == mGpsWidgetRowView;
    }

    public boolean match(int position) {
        return position == 0;
    }

    public void setData(View view, int position) {
    }
}
