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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.activity.cachelist.ActivityVisible;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.inject.ContextScoped;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
@ContextScoped
public class GeocacheListAdapter extends BaseAdapter {
    //TODO(sng): Rename to CacheListAdapter.
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;
    private final GeocacheVectors mGeocacheVectors;
    private final ActivityVisible mActivityVisible;
    private CharSequence mSelected;

    public GeocacheListAdapter(GeocacheVectors geocacheVectors,
            GeocacheSummaryRowInflater geocacheSummaryRowInflater,
            ActivityVisible activityVisible) {
        mGeocacheVectors = geocacheVectors;
        mGeocacheSummaryRowInflater = geocacheSummaryRowInflater;
        mActivityVisible = activityVisible;
    }

    @Inject
    public GeocacheListAdapter(Injector injector) {
        mGeocacheVectors = injector.getInstance(GeocacheVectors.class);
        mGeocacheSummaryRowInflater = injector.getInstance(GeocacheSummaryRowInflater.class);
        mActivityVisible = injector.getInstance(ActivityVisible.class);
    }

    @Override
    public int getCount() {
        return mGeocacheVectors.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mGeocacheSummaryRowInflater.inflate(convertView);
        if (!mActivityVisible.getVisible()) {
            // Log.d("GeoBeagle",
            // "Not visible, punting any real work on getView");
            return view;
        }
        mGeocacheSummaryRowInflater.setData(view, mGeocacheVectors.get(position));
        // ListView listView = (ListView)parent;
        // boolean isChecked = listView.isItemChecked(position + 1);
        boolean isChecked = mGeocacheVectors.get(position).getId().equals(mSelected);
        view.setBackgroundColor(isChecked ? Color.DKGRAY : Color.TRANSPARENT);
        return view;
    }

    public void setSelected(int position) {
        mSelected = mGeocacheVectors.get(position).getId();
    }
}
