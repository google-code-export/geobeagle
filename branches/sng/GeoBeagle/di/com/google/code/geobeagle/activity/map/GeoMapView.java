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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class GeoMapView extends MapView {

    private OverlayManager mOverlayManager;

    public GeoMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("GeoBeagle", "~~~~~~~~~~onLayout " + changed + ", " + left + ", " + top + ", "
                + right + ", " + bottom);
        if (mOverlayManager != null) {
            mOverlayManager.selectOverlay();
        }
    }

    public void setScrollListener(OverlayManager overlayManager) {
        mOverlayManager = overlayManager;
    }

}
