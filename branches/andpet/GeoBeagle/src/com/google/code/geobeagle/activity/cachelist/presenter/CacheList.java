package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.activity.cachelist.view.GeocacheSummaryRowInflater;
import com.google.code.geobeagle.database.CachesProviderToggler;
import com.google.code.geobeagle.database.DistanceAndBearing;
import com.google.code.geobeagle.database.DistanceAndBearing.IDistanceAndBearingProvider;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/** Update the gui cache list when the location or azimuth changes
 * (but not too often) */
public class CacheList extends BaseAdapter implements Refresher {
    //TODO: Should be Lazy
    private final CachesProviderToggler mProvider;
    private final IDistanceAndBearingProvider mDistances;
    private final GeocacheSummaryRowInflater mGeocacheSummaryRowInflater;
    private final TitleUpdater mTitleUpdater;
    //private ArrayList<Geocache> mListData;
    private float mAzimuth;
    private boolean mUpdatesEnabled;

    public CacheList(CachesProviderToggler provider, 
            IDistanceAndBearingProvider distances,
            GeocacheSummaryRowInflater inflater,
            TitleUpdater titleUpdater) {
        mProvider = provider;
        mGeocacheSummaryRowInflater = inflater;
        mTitleUpdater = titleUpdater;
        mDistances = distances;
    }    
    
    public void setLocation(double latitude, double longitude) {
        mProvider.setCenter(latitude, longitude);
    }

    public void enableUpdates(boolean enable) {
        mUpdatesEnabled = enable;
    }
    
    public void setAzimuth (float azimuth) {
        mAzimuth = azimuth;
    }
    
    /** Updates the GUI from the Provider if necessary */
    @Override
    public void refresh() {
        if (!mUpdatesEnabled || !mProvider.hasChanged()) {
            return;
        }

        forceRefresh();
    }

    public void forceRefresh() {
        //mProvider.resetChanged();
        mTitleUpdater.refresh();
        notifyDataSetChanged();
    }
    
    public int getCount() {
        return mProvider.getCount();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mGeocacheSummaryRowInflater.inflate(convertView);
        Geocache cache = mProvider.getCaches().get(position);
        DistanceAndBearing geocacheVector = mDistances.getDistanceAndBearing(cache);
        mGeocacheSummaryRowInflater.setData(view, geocacheVector, mAzimuth);
        return view;
    }
    
}
