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

package com.google.code.geobeagle;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.GeocacheFromPreferencesFactory;
import com.google.code.geobeagle.data.GeocacheFactory.Source;
import com.google.code.geobeagle.intents.GeocacheToCachePage;
import com.google.code.geobeagle.intents.GeocacheToGoogleMap;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentStarterLocation;
import com.google.code.geobeagle.intents.IntentStarterRadar;
import com.google.code.geobeagle.intents.IntentStarterViewUri;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GeocacheViewer;
import com.google.code.geobeagle.ui.GpsStatusWidget;
import com.google.code.geobeagle.ui.MeterView;
import com.google.code.geobeagle.ui.Misc;
import com.google.code.geobeagle.ui.MyLocationProvider;
import com.google.code.geobeagle.ui.OnCacheButtonClickListenerBuilder;
import com.google.code.geobeagle.ui.OnContentProviderSelectedListener;
import com.google.code.geobeagle.ui.UpdateGpsWidgetRunnableDI;
import com.google.code.geobeagle.ui.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController;
import com.google.code.geobeagle.ui.cachelist.GeocacheListOnClickListener;
import com.google.code.geobeagle.ui.cachelist.GeocacheListPresenter.CompassListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends Activity implements LifecycleManager {
    private ContentSelector mContentSelector;
    private final ErrorDisplayer mErrorDisplayer;
    private GeoBeagleDelegate mGeoBeagleDelegate;
    private Geocache mGeocache;
    private GeocacheFactory mGeocacheFactory;
    private GeocacheFromPreferencesFactory mGeocacheFromPreferencesFactory;
    private GeocacheViewer mGeocacheViewer;
    private GpsStatusWidget mGpsStatusWidget;
    private LocationControlBuffered mLocationControlBuffered;
    private LocationSaver mLocationSaver;
    private final ResourceProvider mResourceProvider;
    private CombinedLocationListener mStatusWidgetLocationListener;
    private UpdateGpsWidgetRunnable mUpdateGpsWidgetRunnable;
    private WebPageAndDetailsButtonEnabler mWebPageButtonEnabler;
    private final SQLiteWrapper mSqliteWrapper;
    private final Database mDatabase;
    private SensorManager mSensorManager;
    // private Sensor mCompassSensor;
    private CompassListener mCompassListener;
    private RadarView mRadar;

    public GeoBeagle() {
        super();
        mErrorDisplayer = new ErrorDisplayer(this);
        mResourceProvider = new ResourceProvider(this);

        mSqliteWrapper = new SQLiteWrapper(null);
        mDatabase = DatabaseDI.create(this);
    }

    private void getCoordinatesFromIntent(Intent intent) {
        try {
            if (intent.getType() == null) {
                final String query = intent.getData().getQuery();
                final CharSequence sanitizedQuery = Util.parseHttpUri(query,
                        new UrlQuerySanitizer(), UrlQuerySanitizer
                                .getAllButNulAndAngleBracketsLegal());
                final CharSequence[] latlon = Util.splitLatLonDescription(sanitizedQuery);
                mGeocache = mGeocacheFactory.create(latlon[2], latlon[3], Util
                        .parseCoordinate(latlon[0]), Util.parseCoordinate(latlon[1]),
                        Source.WEB_URL, null);
                mSqliteWrapper.openWritableDatabase(mDatabase);
                mLocationSaver.saveLocation(mGeocache);
                mSqliteWrapper.close();
                mGeocacheViewer.set(mGeocache);
            }
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public Geocache getGeocache() {
        return mGeocache;
    }

    private boolean maybeGetCoordinatesFromIntent() {
        final Intent intent = getIntent();
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_VIEW)) {
                    getCoordinatesFromIntent(intent);
                    return true;
                } else if (action.equals(GeocacheListController.SELECT_CACHE)) {
                    mGeocache = intent.<Geocache> getParcelableExtra("geocache");
                    mGeocacheViewer.set(mGeocache);
                    mWebPageButtonEnabler.check();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0)
            setIntent(data);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return mGeoBeagleDelegate.onCreateDialog(id);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GeoBeagleBuilder builder = new GeoBeagleBuilder(this);
        mContentSelector = builder.createContentSelector(getPreferences(Activity.MODE_PRIVATE));
        mLocationSaver = new LocationSaver(DatabaseDI.createCacheWriter(mSqliteWrapper));
        mWebPageButtonEnabler = Misc.create(this, findViewById(R.id.cache_page),
                findViewById(R.id.cache_details));

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final CombinedLocationManager combinedLocationManager = new CombinedLocationManager(
                locationManager);
        mLocationControlBuffered = LocationControlDi.create(locationManager);
        final Misc.Time time = new Misc.Time();
        MeterView.MeterFormatter meterFormatter = new MeterView.MeterFormatter();
        mGpsStatusWidget = new GpsStatusWidget(this, mResourceProvider, meterFormatter, time,
                combinedLocationManager, mLocationControlBuffered);
        /*
         * mGpsStatusWidget = new GpsStatusWidget(this, mResourceProvider, new
         * MeterView( getTextView(R.id.location_viewer), new MeterFormatter()),
         * getTextView(R.id.provider), getTextView(R.id.lag),
         * getTextView(R.id.accuracy), getTextView(R.id.status), time, new
         * Location(""), combinedLocationManager, mLocationControlBuffered);
         */
        mStatusWidgetLocationListener = new CombinedLocationListener(mLocationControlBuffered,
                mGpsStatusWidget);
        mGeocacheFactory = new GeocacheFactory();
        mGeocacheFromPreferencesFactory = new GeocacheFromPreferencesFactory(mGeocacheFactory);
        final TextView gcid = (TextView)findViewById(R.id.gcid);
        final TextView gcname = (TextView)findViewById(R.id.gcname);
        final TextView gccoords = (TextView)findViewById(R.id.gccoords);
        mRadar = (RadarView)findViewById(R.id.radarview);
        mRadar.setUseMetric(true);
//        mRadar.startSweep();
        mRadar.setDistanceView((TextView)findViewById(R.id.radar_distance),
                (TextView)findViewById(R.id.radar_bearing),
                (TextView)findViewById(R.id.radar_accuracy));
        mGeocacheViewer = new GeocacheViewer(mRadar, gcid, gcname, gccoords);

        mLocationControlBuffered.onLocationChanged(null);
        setCacheClickListeners();

        ((Button)findViewById(R.id.go_to_list)).setOnClickListener(new GeocacheListOnClickListener(
                this));

        // Register for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mRadar);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mRadar);

        AppLifecycleManager appLifecycleManager = new AppLifecycleManager(
                getPreferences(MODE_PRIVATE),
                new LifecycleManager[] {
                        this,
                        new LocationLifecycleManager(mStatusWidgetLocationListener, locationManager),
                        new LocationLifecycleManager(mLocationControlBuffered, locationManager),
                        new LocationLifecycleManager(mRadar, locationManager), mContentSelector
                });
        mGeoBeagleDelegate = GeoBeagleDelegate.buildGeoBeagleDelegate(this, appLifecycleManager,
                mGeocacheViewer, mErrorDisplayer);
        mGeoBeagleDelegate.onCreate();

        ((Spinner)findViewById(R.id.content_provider))
                .setOnItemSelectedListener(new OnContentProviderSelectedListener(mResourceProvider,
                        (TextView)findViewById(R.id.select_cache_prompt)));

        mCompassListener = new CompassListener(new NullRefresher(), mLocationControlBuffered,
                -1440f);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // final List<Sensor> sensorList =
        // mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        // mCompassSensor = sensorList.get(0);

        mUpdateGpsWidgetRunnable = UpdateGpsWidgetRunnableDI.create(mGpsStatusWidget
                .getGpsStatusWidgetDelegate());
        mUpdateGpsWidgetRunnable.run();
        // } catch (final Exception e) {
        // mErrorDisplayer.displayErrorAndStack(e);
        // }
    }

    static class NullRefresher implements Refresher {
        public void refresh() {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mGeoBeagleDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGeoBeagleDelegate.onPause();
        mSensorManager.unregisterListener(mCompassListener);
        mSensorManager.unregisterListener(mRadar);
    }

    public void onPause(Editor editor) {
        getGeocache().writeToPrefs(editor);
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            mRadar.handleUnknownLocation();
            mGeoBeagleDelegate.onResume();
            mSqliteWrapper.openWritableDatabase(mDatabase);
            mSensorManager.registerListener(mCompassListener, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mRadar, SensorManager.SENSOR_ORIENTATION,
                    SensorManager.SENSOR_DELAY_UI);

            // mSensorManager.registerListener(mCompassListener, mCompassSensor,
            // SensorManager.SENSOR_DELAY_UI);

            maybeGetCoordinatesFromIntent();
            mWebPageButtonEnabler.check();
            final Location location = mLocationControlBuffered.getLocation();
            if (location != null)
                mStatusWidgetLocationListener.onLocationChanged(location);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    public void onResume(SharedPreferences preferences) {
        setGeocache(mGeocacheFromPreferencesFactory.create(preferences));
    }

    private void setCacheClickListeners() {
        IntentFactory intentFactory = new IntentFactory(new UriParser());
        Toast getCoordsToast = Toast.makeText(this, R.string.get_coords_toast, Toast.LENGTH_LONG);
        MyLocationProvider myLocationProvider = new MyLocationProvider(mLocationControlBuffered,
                mErrorDisplayer);

        OnCacheButtonClickListenerBuilder cacheClickListenerSetter = new OnCacheButtonClickListenerBuilder(
                this, mErrorDisplayer);

        cacheClickListenerSetter.set(R.id.object_map, new IntentStarterLocation(this,
                mResourceProvider, intentFactory, myLocationProvider, mContentSelector,
                R.array.map_objects, getCoordsToast), "");
        cacheClickListenerSetter.set(R.id.nearest_objects, new IntentStarterLocation(this,
                mResourceProvider, intentFactory, myLocationProvider, mContentSelector,
                R.array.nearest_objects, getCoordsToast), "");

        cacheClickListenerSetter.set(R.id.maps, new IntentStarterViewUri(this, intentFactory,
                mGeocacheViewer, new GeocacheToGoogleMap(mResourceProvider)), "");
        cacheClickListenerSetter.set(R.id.cache_page, new IntentStarterViewUri(this, intentFactory,
                mGeocacheViewer, new GeocacheToCachePage(mResourceProvider)), "");
        cacheClickListenerSetter.set(R.id.radar, new IntentStarterRadar(this),
                "\nPlease install the Radar application to use Radar.");
    }

    void setGeocache(Geocache geocache) {
        mGeocache = geocache;
        mGeocacheViewer.set(getGeocache());
    }
}
