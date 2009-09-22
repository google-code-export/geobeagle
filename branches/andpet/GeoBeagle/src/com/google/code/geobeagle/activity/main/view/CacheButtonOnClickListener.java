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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.CacheAction;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class CacheButtonOnClickListener implements OnClickListener {
    private final CacheAction mCacheAction;
    private final ErrorDisplayer mErrorDisplayer;
    private final String mActivityNotFoundErrorMessage;
    private final GeoBeagle mGeoBeagle;

    public CacheButtonOnClickListener(CacheAction cacheAction, 
            GeoBeagle geoBeagle,
            String errorMessage,
            ErrorDisplayer errorDisplayer) {
        mCacheAction = cacheAction;
        mGeoBeagle = geoBeagle;
        mErrorDisplayer = errorDisplayer;
        mActivityNotFoundErrorMessage = errorMessage;
    }

    public void onClick(View view) {
        Geocache geocache = mGeoBeagle.getGeocache();
        try {
            mCacheAction.act(geocache);
        } catch (final ActivityNotFoundException e) {
            mErrorDisplayer.displayError(R.string.error2, e.getMessage(),
                    mActivityNotFoundErrorMessage);
        } catch (final Exception e) {
            mErrorDisplayer.displayError(R.string.error1, e.getMessage());
        }
    }
}
