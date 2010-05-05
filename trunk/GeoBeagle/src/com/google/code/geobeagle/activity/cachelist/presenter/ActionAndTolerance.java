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

import com.google.code.geobeagle.LocationControlBuffered.IGpsLocation;

public class ActionAndTolerance {
    private final RefreshAction mRefreshAction;
    private final ToleranceStrategy mToleranceStrategy;

    public ActionAndTolerance(RefreshAction refreshAction, ToleranceStrategy toleranceStrategy) {
        mRefreshAction = refreshAction;
        mToleranceStrategy = toleranceStrategy;
    }

    public boolean exceedsTolerance(IGpsLocation here, float azimuth, long now) {
        return mToleranceStrategy.exceedsTolerance(here, azimuth, now);
    }

    public void refresh() {
        mRefreshAction.refresh();
    }

    public void updateLastRefreshed(IGpsLocation here, float azimuth, long now) {
        mToleranceStrategy.updateLastRefreshed(here, azimuth, now);
    }
}