
package com.google.code.geobeagle.activity.map;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.List;

public class DensityOverlay extends Overlay {
    private List<DensityPatch> mDensityPatches;
    private final DensityMatrix mDensityMatrix;

    public DensityOverlay(DensityMatrix matrix) {
        mDensityMatrix = matrix;
        mDensityPatches = mDensityMatrix.getDensityPatches();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow)
            return; // No shadow layer

        final Projection proj = mapView.getProjection();

        Rect tempRect = new Rect();
        Paint bluePaint = new Paint();
        bluePaint.setARGB(128, 255, 0, 0);
        Point screenLow = new Point();
        Point screenHigh = new Point();
        for (DensityPatch patch : mDensityPatches) {
            proj.toPixels(patch.getExtentLow(), screenLow);
            proj.toPixels(patch.getExtentHigh(), screenHigh);
            tempRect.bottom = Math.max(screenLow.y, screenHigh.y);
            tempRect.top = Math.min(screenLow.y, screenHigh.y);
            tempRect.left = Math.min(screenLow.x, screenHigh.x);
            tempRect.right = Math.max(screenLow.x, screenHigh.x);
            int count = patch.getCacheCount();
            // Never draw completely opaque
            bluePaint.setAlpha(Math.min(210, 10 + 32 * count));
            canvas.drawRect(tempRect, bluePaint);
        }
    }

}
