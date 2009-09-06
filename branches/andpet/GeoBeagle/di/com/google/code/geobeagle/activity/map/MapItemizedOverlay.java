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

import com.google.android.maps.ItemizedOverlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.util.ArrayList;

public class MapItemizedOverlay extends ItemizedOverlay<CacheItem> {

    private final CacheItemFactory mCacheItemFactory;
    private final Context mContext;
    private final ArrayList<CacheItem> mOverlays;
	private Handler mGuiThreadHandler;

	/** Execute on the gui thread to avoid ArrayIndexOutOfBoundsException */
    private class CacheListUpdater implements Runnable {
    	ArrayList<Geocache> mCacheList;
    	public CacheListUpdater(ArrayList<Geocache> list) {
    		mCacheList = list;
    	}
    	public void run() {
            mOverlays.clear();
    		for (Geocache cache : mCacheList) {
    			CacheItem item = mCacheItemFactory.createCacheItem(cache);
    			if (item != null)
    				mOverlays.add(item);
    		}
    		populate();
    	}
    };
    
    public MapItemizedOverlay(Context context, Drawable defaultMarker,
            CacheItemFactory cacheItemFactory) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
        mCacheItemFactory = cacheItemFactory;
        mOverlays = new ArrayList<CacheItem>();
        mGuiThreadHandler = new Handler();
    }

    /** Replaces all caches on the map with the supplied ones. */
    public void setCacheListUsingGuiThread(ArrayList<Geocache> list) {
        mGuiThreadHandler.post(new CacheListUpdater(list));
    }

    /*
    public void addCache(CacheItem overlay) {
        mOverlays.add(overlay);
        // populate();
    }
    */

    @Override
    protected CacheItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    protected boolean onTap(int i) {
        Geocache geocache = mOverlays.get(i).getGeocache();
        if (geocache == null)
            return false;

        // Intent intent = new Intent(GeocacheListController.SELECT_CACHE);

        Intent intent = new Intent(mContext, GeoBeagle.class);
        intent.setAction(GeocacheListController.SELECT_CACHE);
        intent.putExtra("geocache", geocache);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        return true;
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
}
