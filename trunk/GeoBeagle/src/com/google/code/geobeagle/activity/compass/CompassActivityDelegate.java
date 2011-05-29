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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.compass.GeocacheFromParcelFactory;
import com.google.code.geobeagle.activity.compass.view.CheckDetailsButton;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.activity.compass.view.WebPageMenuEnabler;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.LocationSaver;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class CompassActivityDelegate {

    static int ACTIVITY_REQUEST_TAKE_PICTURE = 1;
    private final ActivitySaver activitySaver;
    private final AppLifecycleManager appLifecycleManager;
    private final Provider<DbFrontend> dbFrontendProvider;
    private Geocache geocache;
    private final GeocacheFactory geocacheFactory;
    private final GeocacheFromParcelFactory geocacheFromParcelFactory;
    private final GeocacheViewer geocacheViewer;
    private final IncomingIntentHandler incomingIntentHandler;
    private final GeoBeagleActivityMenuActions menuActions;
    private final CompassActivity parent;
    private final CheckDetailsButton checkDetailsButton;
    private final GeoBeagleEnvironment geoBeagleEnvironment;
    private final WebPageMenuEnabler webPageMenuEnabler;
    private final LocationSaver locationSaver;
    private final GeoBeagleSensors geoBeagleSensors;

    public CompassActivityDelegate(ActivitySaver activitySaver,
            AppLifecycleManager appLifecycleManager,
            Activity parent,
            GeocacheFactory geocacheFactory,
            GeocacheViewer geocacheViewer,
            IncomingIntentHandler incomingIntentHandler,
            GeoBeagleActivityMenuActions menuActions,
            GeocacheFromParcelFactory geocacheFromParcelFactory,
            Provider<DbFrontend> dbFrontendProvider,
            CheckDetailsButton checkDetailsButton,
            WebPageMenuEnabler webPageMenuEnabler,
            GeoBeagleEnvironment geoBeagleEnvironment,
            LocationSaver locationSaver,
            GeoBeagleSensors geoBeagleSensors) {
        this.parent = (CompassActivity)parent;
        this.activitySaver = activitySaver;
        this.appLifecycleManager = appLifecycleManager;
        this.menuActions = menuActions;
        this.geocacheViewer = geocacheViewer;
        this.geocacheFactory = geocacheFactory;
        this.incomingIntentHandler = incomingIntentHandler;
        this.dbFrontendProvider = dbFrontendProvider;
        this.geocacheFromParcelFactory = geocacheFromParcelFactory;
        this.geoBeagleEnvironment = geoBeagleEnvironment;
        this.checkDetailsButton = checkDetailsButton;
        this.webPageMenuEnabler = webPageMenuEnabler;
        this.locationSaver = locationSaver;
        this.geoBeagleSensors = geoBeagleSensors;
    }

    @Inject
    public CompassActivityDelegate(Injector injector) {
        parent = (CompassActivity)injector.getInstance(Activity.class);
        activitySaver = injector.getInstance(ActivitySaver.class);
        appLifecycleManager = injector.getInstance(AppLifecycleManager.class);
        menuActions = injector.getInstance(GeoBeagleActivityMenuActions.class);
        geocacheViewer = injector.getInstance(GeocacheViewerFactory.class).create(
                new ActivityViewContainer(parent));
        geocacheFactory = injector.getInstance(GeocacheFactory.class);
        incomingIntentHandler = injector.getInstance(IncomingIntentHandler.class);
        dbFrontendProvider = injector.getProvider(DbFrontend.class);
        geocacheFromParcelFactory = injector.getInstance(GeocacheFromParcelFactory.class);
        geoBeagleEnvironment = injector.getInstance(GeoBeagleEnvironment.class);
        checkDetailsButton = injector.getInstance(CheckDetailsButton.class);
        webPageMenuEnabler = injector.getInstance(WebPageMenuEnabler.class);
        locationSaver = injector.getInstance(LocationSaver.class);
        geoBeagleSensors = injector.getInstance(GeoBeagleSensors.class);
    }

    public Geocache getGeocache() {
        return geocache;
    }

    private void onCameraStart() {
        String filename = geoBeagleEnvironment.getExternalStorageDir() + "/GeoBeagle_"
                + geocache.getId()
                + DateFormat.format("_yyyy-MM-dd_kk.mm.ss.jpg", System.currentTimeMillis());
        Log.d("GeoBeagle", "capturing image to " + filename);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filename)));
        parent.startActivityForResult(intent, CompassActivityDelegate.ACTIVITY_REQUEST_TAKE_PICTURE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return menuActions.onCreateOptionsMenu(menu);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && event.getRepeatCount() == 0) {
            onCameraStart();
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return menuActions.act(item.getItemId());
    }

    public void onPause() {
        appLifecycleManager.onPause();
        geoBeagleSensors.unregisterSensors();
        activitySaver.save(ActivityType.VIEW_CACHE, geocache);
        dbFrontendProvider.get().closeDatabase();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        geocache = geocacheFromParcelFactory.createFromBundle(savedInstanceState);
    }

    public void onResume() {
        appLifecycleManager.onResume();
        geoBeagleSensors.registerSensors();
        geocache = incomingIntentHandler.maybeGetGeocacheFromIntent(parent.getIntent(),
                geocache, locationSaver);

        // Possible fix for issue 53.
        if (geocache == null) {
            geocache = geocacheFactory.create("", "", 0, 0, Source.MY_LOCATION, "",
                    CacheType.NULL, 0, 0, 0, true, false);
        }
        geocacheViewer.set(geocache);
        checkDetailsButton.check(geocache);
    }

    public void onSaveInstanceState(Bundle outState) {
        // apparently there are cases where getGeocache returns null, causing
        // crashes with 0.7.7/0.7.8.
        if (geocache != null)
            geocache.saveToBundle(outState);
    }

    public void setGeocache(Geocache geocache) {
        this.geocache = geocache;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.string.web_page);
        item.setVisible(webPageMenuEnabler.shouldEnable(getGeocache()));
        return true;
    }
}
