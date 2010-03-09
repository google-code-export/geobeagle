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
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.main.GeoBeagleDelegate.LogFindClickListener;
import com.google.code.geobeagle.activity.main.fieldnotes.CacheLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.DateFormatter;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperCommon;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperFile;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperSms;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteStringsFVsDnf;
import com.google.code.geobeagle.activity.main.fieldnotes.FileLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.SmsLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickCancel;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.activity.main.intents.GeocacheToCachePage;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleMap;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionGoogleMaps;
import com.google.code.geobeagle.activity.main.view.CacheButtonOnClickListener;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;
import com.google.code.geobeagle.activity.main.view.OnCacheButtonClickListenerBuilder;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.code.geobeagle.activity.map.GeoMapActivity;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.location.LocationLifecycleManager;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.Inject;

import roboguice.activity.GuiceActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends GuiceActivity {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm'Z'");
    private GeoBeagleDelegate mGeoBeagleDelegate;
    private DbFrontend mDbFrontend;
    private FieldnoteLogger mFieldNoteSender;
    private static final DateFormat mLocalDateFormat = DateFormat
            .getTimeInstance(DateFormat.MEDIUM);
    
    @Inject
    LocationControlBuffered locationControlBuffered;
    
    @Inject
    ActivitySaver activitySaver;

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
                (ImageView)findViewById(R.id.gcicon), gcDifficulty, gcTerrain, gcContainer);
        mDbFrontend = new DbFrontend(this);

        locationControlBuffered.onLocationChanged(null);
        final IntentFactory intentFactory = new IntentFactory();

        // Register for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, radar);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, radar);

        final AppLifecycleManager appLifecycleManager = new AppLifecycleManager(
                getPreferences(MODE_PRIVATE), new LifecycleManager[] {
                        new LocationLifecycleManager(locationControlBuffered, locationManager),
                        new LocationLifecycleManager(radar, locationManager)
                });

        final IntentStarterViewUri intentStarterViewUri = new IntentStarterViewUri(this,
                intentFactory, new GeocacheToGoogleMap(this));
        final SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        final CompassListener compassListener = new CompassListener(new NullRefresher(),
                locationControlBuffered, -1440f);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(this), new MenuActionEditGeocache(this),
                // new MenuActionLogDnf(this), new MenuActionLogFind(this),
                new MenuActionSearchOnline(this), new MenuActionSettings(this),
                new MenuActionGoogleMaps(intentStarterViewUri)
        };
        final MenuActions menuActions = new MenuActions(getResources(), menuActionArray);
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        final GeocacheFromIntentFactory geocacheFromIntentFactory = new GeocacheFromIntentFactory(
                geocacheFactory, mDbFrontend);
        final IncomingIntentHandler incomingIntentHandler = new IncomingIntentHandler(
                geocacheFactory, geocacheFromIntentFactory);
        final GeocacheFromParcelFactory geocacheFromParcelFactory = new GeocacheFromParcelFactory(
                geocacheFactory);
        mGeoBeagleDelegate = new GeoBeagleDelegate(activitySaver, appLifecycleManager,
                compassListener, this, geocacheFactory, geocacheViewer, incomingIntentHandler,
                menuActions, geocacheFromParcelFactory, mDbFrontend, radar, sensorManager,
                defaultSharedPreferences, webPageButtonEnabler);

        // see http://www.androidguys.com/2008/11/07/rotational-forces-part-two/
        if (getLastNonConfigurationInstance() != null) {
            setIntent((Intent)getLastNonConfigurationInstance());
        }

        final Intent geoMapActivityIntent = new Intent(this, GeoMapActivity.class);
        final IntentStarterGeo intentStarterMapActivity = new IntentStarterGeo(this,
                geoMapActivityIntent);
        final CacheButtonOnClickListener mapsButtonOnClickListener = new CacheButtonOnClickListener(
                intentStarterMapActivity, "Map error", errorDisplayer);
        findViewById(id.maps).setOnClickListener(mapsButtonOnClickListener);

        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(this);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc
                .createCacheDetailsOnClickListener(this, cacheDetailsBuilder, layoutInflater);

        findViewById(R.id.cache_details).setOnClickListener(cacheDetailsOnClickListener);

        final GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(getResources());
        final IntentStarterViewUri cachePageIntentStarter = new IntentStarterViewUri(this,
                intentFactory, geocacheToCachePage);
        final CacheButtonOnClickListener cacheButtonOnClickListener = new CacheButtonOnClickListener(
                cachePageIntentStarter, "", errorDisplayer);
        findViewById(id.cache_page).setOnClickListener(cacheButtonOnClickListener);

        final OnCacheButtonClickListenerBuilder cacheClickListenerSetter = new OnCacheButtonClickListenerBuilder(
                this, errorDisplayer);
        cacheClickListenerSetter.set(id.radarview, new IntentStarterGeo(this, new Intent(
                "com.google.android.radar.SHOW_RADAR")),
                "Please install the Radar application to use Radar.");

        findViewById(id.menu_log_find).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_find));
        findViewById(id.menu_log_dnf).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_dnf));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        final FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = new FieldnoteStringsFVsDnf(
                getResources());
        final Toaster toaster = new Toaster(this, R.string.error_writing_cache_log,
                Toast.LENGTH_LONG);
        final DateFormatter dateFormatter = new DateFormatter(simpleDateFormat);
        final FileLogger fileLogger = new FileLogger(fieldnoteStringsFVsDnf, dateFormatter, toaster);
        final SmsLogger smsLogger = new SmsLogger(fieldnoteStringsFVsDnf, this);
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        final CacheLogger cacheLogger = new CacheLogger(defaultSharedPreferences, fileLogger,
                smsLogger);
        final OnClickCancel onClickCancel = new OnClickCancel();
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View fieldNoteDialogView = layoutInflater.inflate(R.layout.fieldnote, null);
        final TextView fieldnoteCaveat = (TextView)fieldNoteDialogView
                .findViewById(R.id.fieldnote_caveat);

        final CharSequence geocacheId = mGeoBeagleDelegate.getGeocache().getId();
        final boolean fDnf = id == R.id.menu_log_dnf;
        final EditText editText = (EditText)fieldNoteDialogView.findViewById(R.id.fieldnote);
        final DialogHelperCommon dialogHelperCommon = new DialogHelperCommon(
                fieldnoteStringsFVsDnf, editText, fDnf, fieldnoteCaveat);

        final DialogHelperFile dialogHelperFile = new DialogHelperFile(fieldnoteCaveat, this);
        final DialogHelperSms dialogHelperSms = new DialogHelperSms(geocacheId.length(),
                fieldnoteStringsFVsDnf, editText, fDnf, fieldnoteCaveat);

        mFieldNoteSender = new FieldnoteLogger(dialogHelperCommon, dialogHelperFile,
                dialogHelperSms);

        final OnClickOk onClickOk = new OnClickOk(geocacheId, editText, cacheLogger, fDnf);
        builder.setTitle(R.string.field_note_title);
        builder.setView(fieldNoteDialogView);
        builder.setNegativeButton(R.string.cancel, onClickCancel);
        builder.setPositiveButton(R.string.log_cache, onClickOk);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onCreateDialog(id);
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mFieldNoteSender.onPrepareDialog(dialog, defaultSharedPreferences, mLocalDateFormat
                .format(new Date()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mGeoBeagleDelegate.onCreateOptionsMenu(menu);
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
