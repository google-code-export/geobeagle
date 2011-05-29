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

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.ListView;

public class ListActivityOnCreateHandler implements ListFragtivityOnCreateHandler {
    private final GeocacheListAdapter geocacheListAdapter;

    @Inject
    public ListActivityOnCreateHandler(GeocacheListAdapter geocacheListAdapter) {
        this.geocacheListAdapter = geocacheListAdapter;
    }

    @Override
    public void onCreateActivity(Activity activity, GeocacheListPresenter geocacheListPresenter) {
        ListActivity listActivity = (ListActivity)activity;
        listActivity.setContentView(R.layout.cache_list);
        ListView listView = listActivity.getListView();
        geocacheListPresenter.setupListView(listView);
        listActivity.setListAdapter(geocacheListAdapter);
    }

    @Override
    public void onCreateFragment(GeocacheListPresenter geocacheListPresenter,
            Object listFragmentParam) {
    }
}
