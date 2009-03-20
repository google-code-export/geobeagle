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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.IGeocacheVector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeocacheListAdapter extends BaseAdapter {

    public static class GeocacheListAdapterDelegate {
        static class ViewHolder {
            TextView mCache;
            TextView mDistance;
        }

        private final Context mContext;
        private final GeocacheVectors mGeocacheVectors;

        public GeocacheListAdapterDelegate(Context context, GeocacheVectors geocacheVectors) {
            mContext = context;
            mGeocacheVectors = geocacheVectors;
        }

        // TODO need to figure out how to test this.
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.cache_row, null);
                viewHolder = new GeocacheListAdapterDelegate.ViewHolder();
                viewHolder.mCache = (TextView)convertView.findViewById(R.id.txt_cache);
                viewHolder.mDistance = (TextView)convertView.findViewById(R.id.distance);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final IGeocacheVector geocacheVector = mGeocacheVectors.get(position);
            viewHolder.mCache.setText(geocacheVector.getIdAndName());
            viewHolder.mDistance.setText(geocacheVector.getFormattedDistance());
            return convertView;
        }

        public int getCount() {
            return mGeocacheVectors.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    private GeocacheListAdapterDelegate mGeocacheListAdapterDelegate;

    public GeocacheListAdapter(Context context, GeocacheVectors geocacheVectors) {
        mGeocacheListAdapterDelegate = new GeocacheListAdapterDelegate(context, geocacheVectors);
    }

    public int getCount() {
        return mGeocacheListAdapterDelegate.getCount();
    }

    public Object getItem(int position) {
        return mGeocacheListAdapterDelegate.getItem(position);
    }

    public long getItemId(int position) {
        return mGeocacheListAdapterDelegate.getItemId(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return mGeocacheListAdapterDelegate.getView(position, convertView, parent);
    }
}
