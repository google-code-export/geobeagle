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

package com.google.code.geobeagle.database;

import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.GeocacheListPrecomputed;
import com.google.code.geobeagle.Toaster.OneTimeToaster;

public class PeggedCacheProvider {
    /** True if the last getCaches() was capped because too high cache count */
    private boolean mTooManyCaches = false;
    private final OneTimeToaster mOneTimeToaster;

    public PeggedCacheProvider(OneTimeToaster oneTimeToaster) {
        mOneTimeToaster = oneTimeToaster;
    }

    GeocacheList pegCaches(int maxCount, GeocacheList caches) {
        mTooManyCaches = (caches.size() > maxCount);
        if (mTooManyCaches) {
            return GeocacheListPrecomputed.EMPTY;
        }
        return caches;
    }

    void showToastIfTooManyCaches() {
        mOneTimeToaster.showToast(mTooManyCaches);
    }

    public boolean isTooManyCaches() {
        return mTooManyCaches;
    }
}
