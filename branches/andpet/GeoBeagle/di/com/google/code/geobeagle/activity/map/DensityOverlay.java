package com.google.code.geobeagle.activity.map;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.google.code.geobeagle.activity.map.DensityMatrix.DensityPatch;

public class DensityOverlay extends Overlay {
	private List<DensityPatch> mDensityPatches;
	private Handler mGuiThreadHandler;

	/** Execute on the gui thread to avoid ArrayIndexOutOfBoundsException */
    private class CacheListUpdater implements Runnable {
    	DensityMatrix mDensityMatrix;
    	public CacheListUpdater(DensityMatrix densityMatrix) {
    		mDensityMatrix = densityMatrix;
    	}
    	public void run() {
    		if (mDensityMatrix != null) {
    			mDensityPatches = mDensityMatrix.getDensityPatches();
    		} else {
    			mDensityPatches = null;
    		}
    	}
    };

    
	public DensityOverlay() {
		mGuiThreadHandler = new Handler();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow)
			return;  //No shadow layer
		if (mDensityPatches == null)
			return; //No patches
		
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
			bluePaint.setAlpha(Math.min(210, 10+32*count));  //Never draw completely opaque
			canvas.drawRect(tempRect, bluePaint);
		}
	}

    
    /** Replaces all caches on the map with the supplied ones. */
    public void setCacheListUsingGuiThread(DensityMatrix matrix) {
        mGuiThreadHandler.post(new CacheListUpdater(matrix));
    }
}
