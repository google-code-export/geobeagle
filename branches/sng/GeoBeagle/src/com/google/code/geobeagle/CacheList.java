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

package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.LocationBookmarks;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class CacheList extends ListActivity {
    public static final String SELECT_CACHE = CacheListDelegate.SELECT_CACHE;
    private CacheListDelegate mCacheListDelegate;

    @Override
    protected void onResume() {
        super.onResume();
        mCacheListDelegate.onResume();
    }

    private final ErrorDisplayer mErrorDisplayer;
    private final ResourceProvider mResourceProvider;

    public CacheList() {
        mErrorDisplayer = new ErrorDisplayer(this);
        mResourceProvider = new ResourceProvider(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        LocationBookmarks locationBookmarks = new LocationBookmarks(this, descriptionsAndLocations,
                Destination.getDestinationPatterns(mResourceProvider));

        mCacheListDelegate = new CacheListDelegate(this, descriptionsAndLocations
                .getPreviousLocations(), locationBookmarks, mErrorDisplayer);

        mCacheListDelegate.onCreate();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(l, v, position, id);
    }

}
