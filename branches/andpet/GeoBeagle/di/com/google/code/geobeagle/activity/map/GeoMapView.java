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

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class GeoMapView extends MapView {

	private GeoMapActivityDelegate mListener;
	
	public GeoMapView(Context context, String mapKey) {
		super(context, mapKey);
	}

	public GeoMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GeoMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollListener(GeoMapActivityDelegate listener) {
		mListener = listener;
	}
	
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	if (changed && mListener != null) {
    		if (left != 0 || right != 0 || top != 0 || bottom != 0) {
    			mListener.onLayoutChange();
    		}
    	}
    }
    
	@Override
	public boolean onTouchEvent(android.view.MotionEvent ev) {
		boolean result = super.onTouchEvent(ev);
		if (ev.getAction() == android.view.MotionEvent.ACTION_UP
			&& mListener != null) {
			mListener.onScrollChange();
		}
		return result;
	}

}
