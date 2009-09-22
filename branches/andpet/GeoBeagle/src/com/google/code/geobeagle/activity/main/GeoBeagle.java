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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.ErrorDisplayerDi;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.actions.CacheActionMap;
import com.google.code.geobeagle.actions.CacheActionRadar;
import com.google.code.geobeagle.actions.CacheActionViewUri;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionGoogleMaps;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.main.GeoBeagleDelegate.LogFindClickListener;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSenderDI;
import com.google.code.geobeagle.activity.main.intents.GeocacheToCachePage;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleMap;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;

import com.google.code.geobeagle.activity.main.view.CacheButtonOnClickListener;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.location.LocationLifecycleManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends Activity {
    private GeoBeagleDelegate mGeoBeagleDelegate;
    private DbFrontend mDbFrontend;

    public Geocache getGeocache() {
        return mGeoBeagleDelegate.getGeocache();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GeoBeagleDelegate.ACTIVITY_REQUEST_TAKE_PICTURE) {
            Log.d("GeoBeagle", "camera intent has returned.");
        } else if (resultCode == 0)
            setIntent(data);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("GeoBeagle", "GeoBeagle onCreate");

        setContentView(R.layout.main);
        final ErrorDisplayer errorDisplayer = ErrorDisplayerDi.createErrorDisplayer(this);
        final WebPageAndDetailsButtonEnabler webPageButtonEnabler = Misc.create(this,
                findViewById(R.id.cache_page), findViewById(R.id.cache_details));
        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final LocationControlBuffered locationControlBuffered = LocationControlDi
                .create(locationManager);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final TextView gcid = (TextView)findViewById(R.id.gcid);
        final AttributeViewer gcDifficulty = new LabelledAttributeViewer(
                GeocacheViewer.STAR_IMAGES, (TextView)findViewById(R.id.gc_text_difficulty),
                (ImageView)findViewById(R.id.gc_difficulty));
        final AttributeViewer gcTerrain = new LabelledAttributeViewer(GeocacheViewer.STAR_IMAGES,
                (TextView)findViewById(R.id.gc_text_terrain),
                (ImageView)findViewById(R.id.gc_terrain));
        final UnlabelledAttributeViewer gcContainer = new UnlabelledAttributeViewer(
                GeocacheViewer.CONTAINER_IMAGES, (ImageView)findViewById(R.id.gccontainer));
        final NameViewer gcName = new NameViewer(((TextView)findViewById(R.id.gcname)));
        final RadarView radar = (RadarView)findViewById(R.id.radarview);
        radar.setUseImperial(false);
        radar.setDistanceView((TextView)findViewById(R.id.radar_distance),
                (TextView)findViewById(R.id.radar_bearing),
                (TextView)findViewById(R.id.radar_accuracy));
        final GeocacheViewer geocacheViewer = new GeocacheViewer(radar, gcid, gcName,
                (ImageView)findViewById(R.id.gcicon),
                gcDifficulty, gcTerrain, gcContainer);

        locationControlBuffered.onLocationChanged(null);
        final IntentFactory intentFactory = new IntentFactory(new UriParser());

        // Register for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, radar);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, radar);

        final AppLifecycleManager appLifecycleManager = new AppLifecycleManager(
                getPreferences(MODE_PRIVATE), new LifecycleManager[] {
                        new LocationLifecycleManager(locationControlBuffered, locationManager),
                        new LocationLifecycleManager(radar, locationManager)
                });

        final CacheActionViewUri intentStarterViewUri = new CacheActionViewUri(this,
                intentFactory, new GeocacheToGoogleMap(this));
        final SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        final CompassListener compassListener = new CompassListener(new NullRefresher(),
                locationControlBuffered, -1440f);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final FieldNoteSender fieldNoteSender = FieldNoteSenderDI.build(this, layoutInflater);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(this);
        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(this), new MenuActionEditGeocache(this),
//                new MenuActionLogDnf(this), new MenuActionLogFind(this),
                new MenuActionSearchOnline(this), new MenuActionSettings(this),
                new MenuActionGoogleMaps(this, intentStarterViewUri)
        };
        final MenuActions menuActions = new MenuActions(getResources(), menuActionArray);
        final Resources resources = this.getResources();
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        final GeocacheFromIntentFactory geocacheFromIntentFactory = new GeocacheFromIntentFactory(
                geocacheFactory, mDbFrontend);
        final IncomingIntentHandler incomingIntentHandler = new IncomingIntentHandler(
                geocacheFactory, geocacheFromIntentFactory);
        final GeocacheFromParcelFactory geocacheFromParcelFactory = new GeocacheFromParcelFactory(
                geocacheFactory);
        mDbFrontend = new DbFrontend(this);
        mGeoBeagleDelegate = new GeoBeagleDelegate(activitySaver, appLifecycleManager,
                compassListener, fieldNoteSender, this, geocacheFactory, geocacheViewer,
                incomingIntentHandler, menuActions, geocacheFromParcelFactory,
                mDbFrontend, radar, resources, sensorManager, defaultSharedPreferences,
                webPageButtonEnabler);

        // see http://www.androidguys.com/2008/11/07/rotational-forces-part-two/
        if (getLastNonConfigurationInstance() != null) {
            setIntent((Intent)getLastNonConfigurationInstance());
        }

        final CacheActionMap cacheActionMap = new CacheActionMap(this);
        final CacheButtonOnClickListener mapsButtonOnClickListener = 
            new CacheButtonOnClickListener(cacheActionMap, this, "Map error", errorDisplayer);
        findViewById(id.maps).setOnClickListener(mapsButtonOnClickListener);

        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(this);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc
                .createCacheDetailsOnClickListener(this, cacheDetailsBuilder, layoutInflater);

        findViewById(R.id.cache_details).setOnClickListener(cacheDetailsOnClickListener);

        final GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(getResources());
        final CacheActionViewUri cachePageIntentStarter = new CacheActionViewUri(this,
                intentFactory, geocacheToCachePage);
        final CacheButtonOnClickListener cacheButtonOnClickListener = 
            new CacheButtonOnClickListener(cachePageIntentStarter, this, "", errorDisplayer);
        findViewById(id.cache_page).setOnClickListener(cacheButtonOnClickListener);

        findViewById(id.radarview).setOnClickListener(new CacheButtonOnClickListener(
                new CacheActionRadar(this), this, "Please install the Radar application to use Radar.", 
                errorDisplayer));

        findViewById(id.menu_log_find).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_find));
        findViewById(id.menu_log_dnf).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_dnf));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        return mGeoBeagleDelegate.onCreateDialog(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mGeoBeagleDelegate.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        //return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mGeoBeagleDelegate.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mGeoBeagleDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GeoBeagle", "GeoBeagle onPause");
        mGeoBeagleDelegate.onPause();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGeoBeagleDelegate.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "GeoBeagle onResume");
        mGeoBeagleDelegate.onResume();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return getIntent();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGeoBeagleDelegate.onSaveInstanceState(outState);
    }
}
