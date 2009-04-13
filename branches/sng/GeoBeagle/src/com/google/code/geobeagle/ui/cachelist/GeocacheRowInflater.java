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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.IGeocacheVector;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class GeocacheRowInflater {
    public static class RowViews {
        private final TextView mCache;
        private final TextView mDistance;

        public RowViews(TextView cache, TextView txtDistance) {
            mCache = cache;
            mDistance = txtDistance;
        }

        public void set(IGeocacheVector geocacheVector) {
            mCache.setText(geocacheVector.getIdAndName());
            mDistance.setText(geocacheVector.getFormattedDistance());
        }
    }

    private final LayoutInflater mLayoutInflater;

    public GeocacheRowInflater(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
    }

    public View inflateIfNecessary(int position, View convertView) {
        // if (position == 0) {
        // return mLayoutInflater.inflate(R.layout.gps_widget, null);
        // }

        if (convertView != null && convertView.getTag() != null) {
            return convertView;
        }

        convertView = mLayoutInflater.inflate(R.layout.cache_row, null);
        RowViews rowViews = new RowViews(((TextView)convertView.findViewById(R.id.txt_cache)),
                ((TextView)convertView.findViewById(R.id.distance)));
        convertView.setTag(rowViews);
        return convertView;
    }
}
