package com.google.code.geobeagle.activity.map;



import com.google.android.maps.ItemizedOverlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;



public class MapItemizedOverlay extends ItemizedOverlay<CacheItem> {



    private ArrayList<CacheItem> mOverlays = new ArrayList<CacheItem>();

    private Context mContext;

    

    public MapItemizedOverlay(Context context, Drawable defaultMarker) {

        super(boundCenter(defaultMarker));

        mContext = context;

    }



    public void clearOverlays() {

        mOverlays.clear();

        populate();

    }

    

    public void addOverlay(CacheItem overlay) {

        mOverlays.add(overlay);

        //populate();

    }



    public void doPopulate() {

        populate();

    }

    

    @Override

    protected CacheItem createItem(int i) {

      return mOverlays.get(i);

    }

    

    @Override

    public int size() {

        return mOverlays.size();

    }



    @Override

    protected boolean onTap(int i) {

        Geocache geocache = mOverlays.get(i).getGeocache();

        if (geocache == null)

            return false;



        //Intent intent = new Intent(GeocacheListController.SELECT_CACHE);

        Intent intent = new Intent(mContext, GeoBeagle.class);

        intent.setAction(GeocacheListController.SELECT_CACHE);

        intent.putExtra("geocache", geocache);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

        mContext.startActivity(intent);

        

        return true;    

    }

}



