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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.IGeocacheVector;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class GeocacheSummaryRowInflater implements RowInflater {
    static class RowViews implements RowInfo {
        private final TextView mCache;
        private final TextView mDistance;

        RowViews(TextView cache, TextView distance) {
            mCache = cache;
            mDistance = distance;
        }

        public RowType getType() {
            return RowType.CacheRow;
        }

        void set(IGeocacheVector geocacheVector) {
            mCache.setText(geocacheVector.getIdAndName());
            mDistance.setText(geocacheVector.getFormattedDistance());
        }
    }

    private final GeocacheVectors mGeocacheVectors;
    private final LayoutInflater mLayoutInflater;

    GeocacheSummaryRowInflater(LayoutInflater layoutInflater, GeocacheVectors geocacheVectors) {
        mLayoutInflater = layoutInflater;
        mGeocacheVectors = geocacheVectors;
    }

    public View inflate(View convertView) {
        if (isAlreadyInflated(convertView))
            return convertView;

        View view = mLayoutInflater.inflate(R.layout.cache_row, null);
        GeocacheSummaryRowInflater.RowViews rowViews = new RowViews(((TextView)view
                .findViewById(R.id.txt_cache)), ((TextView)view.findViewById(R.id.distance)));
        view.setTag(rowViews);
        return view;
    }

    public boolean isAlreadyInflated(View convertView) {
        return convertView != null && ((RowInfo)convertView.getTag()).getType() == RowType.CacheRow;
    }

    public boolean match(int position) {
        return position > 0;
    }

    public void setData(View view, int position) {
        ((RowViews)view.getTag()).set(mGeocacheVectors.get(position - 1));
    }
}
