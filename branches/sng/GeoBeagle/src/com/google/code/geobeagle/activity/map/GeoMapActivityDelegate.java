
package com.google.code.geobeagle.activity.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheList;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.GeocachesSql;
import com.google.code.geobeagle.database.ISQLiteDatabase;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.code.geobeagle.database.DatabaseDI.SearchFactory;
import com.google.code.geobeagle.database.WhereFactoryNearestCaches.WhereStringFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class GeoMapActivityDelegate {
    private Activity mParent;
    private MapView mMapView;
    private MapController mController;
    private Context mContext;
    private boolean mIsSatelliteView = false;

    private List<Overlay> mMapOverlays;
    private MapItemizedOverlay mCachesOverlay;
    private MyLocationOverlay mMyLocationOverlay;

    public GeoMapActivityDelegate(Activity parent, MapView mapView, Context context) {
        mParent = parent;
        mMapView = mapView;
        mController = mapView.getController();
        mContext = context;
    }

    public void onCreate(Bundle savedInstanceState, GeoBeagleSqliteOpenHelper open) {
        Intent i = mParent.getIntent();
        double latitude = i.getDoubleExtra("latitude", 0);
        double longitude = i.getDoubleExtra("longitude", 0);
        mMapView.setBuiltInZoomControls(true);

        // mMapView.setOnLongClickListener()
        mMapView.setSatellite(mIsSatelliteView);
        mController = mMapView.getController();

        // mController.setZoom(14);
        GeoPoint center = new GeoPoint((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));

        mController.setCenter(center);
        mMapOverlays = mMapView.getOverlays();

        Drawable drawable = mContext.getResources().getDrawable(R.drawable.map_others);
        mCachesOverlay = new MapItemizedOverlay(mContext, drawable);
        mCachesOverlay.doPopulate();

        mMapOverlays.add(mCachesOverlay);
        mMyLocationOverlay = new MyLocationOverlay(mContext, mMapView);
        mMapOverlays.add(mMyLocationOverlay);
        
        refreshCaches(latitude, longitude, open);
    }

    public void onResume() {
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableCompass();
    }

    public void onPause() {
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableCompass();
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        String satStr;
        if (mIsSatelliteView)
            satStr = "Map view";
        else
            satStr = "Satellite view";

        menu.findItem(R.id.menu_toggle_satellite).setTitle(satStr);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_toggle_satellite) {
            mIsSatelliteView = !mIsSatelliteView;
            mMapView.setSatellite(mIsSatelliteView);
            return true;
        }

        if (item.getItemId() == R.id.menu_cache_list) {
            mParent.startActivity(new Intent(mContext, CacheList.class));
            return true;
        }

        return false;
    }

    public void refreshCaches(double latitude, double longitude, GeoBeagleSqliteOpenHelper open) {
        mCachesOverlay.clearOverlays();
        SQLiteDatabase sqDb = open.getReadableDatabase();
        ISQLiteDatabase database = new DatabaseDI.SQLiteWrapper(sqDb);
        GeocachesSql sql = DatabaseDI.createGeocachesSql(database);
        WhereStringFactory whereStringFactory = new WhereStringFactory();
        SearchFactory searchFactory = new SearchFactory();
        sql.loadCaches(latitude, longitude, new WhereFactoryNearestCaches(searchFactory,
                whereStringFactory));
        ArrayList<Geocache> list = sql.getGeocaches();

        for (Geocache cache : list) {
            CacheItem item = CacheItem.Create(mContext.getResources(), cache);
            if (item != null)
                mCachesOverlay.addOverlay(item);
        }
        open.close();
        mCachesOverlay.doPopulate();
    }
}
