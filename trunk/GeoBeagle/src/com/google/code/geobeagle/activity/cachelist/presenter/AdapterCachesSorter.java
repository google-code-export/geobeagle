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

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.Timing;
import com.google.code.geobeagle.activity.cachelist.model.CacheListData;
import com.google.inject.Inject;

public class AdapterCachesSorter implements RefreshAction {
    private final CacheListData mCacheListData;
    private final LocationControlBuffered mLocationControlBuffered;
    private final Timing mTiming;

    @Inject
    public AdapterCachesSorter(CacheListData cacheListData, Timing timing,
            LocationControlBuffered locationControlBuffered) {
        mCacheListData = cacheListData;
        mTiming = timing;
        mLocationControlBuffered = locationControlBuffered;
    }

    public void refresh() {
        mLocationControlBuffered.getSortStrategy().sort(mCacheListData.get());
        mTiming.lap("sort time");
//        Debug.stopMethodTracing();
    }
}