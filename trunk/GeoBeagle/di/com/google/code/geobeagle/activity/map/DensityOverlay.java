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
import com.google.android.maps.Overlay;
import com.google.inject.Inject;

import android.graphics.Canvas;

public class DensityOverlay extends Overlay {
    private DensityOverlayDelegate mDelegate;

    @Inject
    public DensityOverlay(DensityOverlayDelegate densityOverlayDelegate) {
        mDelegate = densityOverlayDelegate;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        mDelegate.draw(canvas, mapView, shadow);
    }
}
