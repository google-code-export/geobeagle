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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Toaster.OneTimeToaster;
import com.google.code.geobeagle.database.CachesProviderLazyArea;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.List;

public class DensityOverlay extends Overlay {
    // Create delegate because it's not possible to test classes that extend
    // Android classes.

    public static DensityOverlayDelegate createDelegate(List<DensityMatrix.DensityPatch> patches,
             GeoPoint nullGeoPoint, CachesProviderLazyArea lazyArea, OneTimeToaster densityOverlayToaster) {
        final Rect patchRect = new Rect();
        final Paint paint = new Paint();
        paint.setARGB(128, 255, 0, 0);
        final Point screenLow = new Point();
        final Point screenHigh = new Point();
        final DensityPatchManager densityPatchManager = new DensityPatchManager(patches,
                lazyArea, densityOverlayToaster);
        return new DensityOverlayDelegate(patchRect, paint, screenLow, screenHigh, 
                densityPatchManager);
    }

    private DensityOverlayDelegate mDelegate;

    public DensityOverlay(DensityOverlayDelegate densityOverlayDelegate) {
        mDelegate = densityOverlayDelegate;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        mDelegate.draw(canvas, mapView, shadow);
    }
}
