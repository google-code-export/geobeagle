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

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.MapView;

public class ZoomSupervisor {
	/** Millisec to wait between polling MapView for changes in zoom level */
	public static final int POLL_INTERVAL_MS = 2000;
	
	private MapView mMapView;
	private GeoMapActivityDelegate mListener;
	private Timer mTimer;
	private int mZoomLevel;
	private class Checker extends TimerTask {
		@Override
		public void run() {
			int newZoom = mMapView.getZoomLevel();
			if (newZoom != mZoomLevel) {
				mListener.onZoomChange(mZoomLevel, newZoom);
				mZoomLevel = newZoom;
			}
		}
	};
	private Checker mChecker;
	
	public ZoomSupervisor(MapView mapView, GeoMapActivityDelegate listener) {
		mMapView = mapView;
		mListener = listener;
	}

	public void start() {
		assert (mTimer == null);
		mZoomLevel = mMapView.getZoomLevel();
		mTimer = new Timer();
		mChecker = new Checker();
		mTimer.schedule(mChecker, POLL_INTERVAL_MS, POLL_INTERVAL_MS);
	}
	
	public void stop() {
		if (mTimer == null)
			return;
		mTimer.cancel();
		mTimer = null;
		mChecker = null;
	}
}
