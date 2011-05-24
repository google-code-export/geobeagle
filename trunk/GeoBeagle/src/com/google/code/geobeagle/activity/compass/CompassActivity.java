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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.LogFindDialogHelper;
import com.google.code.geobeagle.activity.map.OnClickListenerMapPage;
import com.google.inject.Inject;
import com.google.inject.Injector;

import roboguice.activity.GuiceActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class CompassActivity extends GuiceActivity {
    private CompassActivityDelegate compassActivityDelegate;
    private LogFindDialogHelper logFindDialogHelper;
    
    @Inject
    LocationControlBuffered locationControlBuffered;

    public Geocache getGeocache() {
        return compassActivityDelegate.getGeocache();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("GeoBeagle", "GeoBeagle onCreate");

        Injector injector = getInjector();
        injector.getInstance(CompassFragtivityOnCreateHandler.class).onCreate(this);

        RadarView radarView = injector.getInstance(RadarView.class);

        locationControlBuffered.onLocationChanged(null);

        LocationManager locationManager = injector.getInstance(LocationManager.class);

        // Register for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, radarView);
        locationManager
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, radarView);

        compassActivityDelegate = injector.getInstance(CompassActivityDelegate.class);

        // see http://www.androidguys.com/2008/11/07/rotational-forces-part-two/
        if (getLastNonConfigurationInstance() != null) {
            setIntent((Intent)getLastNonConfigurationInstance());
        }
        OnClickListenerMapPage onClickListenerMapPage = injector
                .getInstance(OnClickListenerMapPage.class);
        findViewById(R.id.maps).setOnClickListener(onClickListenerMapPage);

        injector.getInstance(CompassClickListenerSetter.class).setListeners(
                new ActivityViewContainer(this), this);
        
        logFindDialogHelper = injector.getInstance(LogFindDialogHelper.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return compassActivityDelegate.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (compassActivityDelegate.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return compassActivityDelegate.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        Log.d("GeoBeagle", "GeoBeagle onPause");
        compassActivityDelegate.onPause();
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return compassActivityDelegate.onPrepareOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CompassActivityDelegate.ACTIVITY_REQUEST_TAKE_PICTURE) {
            Log.d("GeoBeagle", "camera intent has returned.");
        } else if (resultCode == Activity.RESULT_OK)
            setIntent(data);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        return logFindDialogHelper.onCreateDialog(this, id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        logFindDialogHelper.onPrepareDialog(this, id, dialog);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        compassActivityDelegate.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "GeoBeagle onResume");
        compassActivityDelegate.onResume();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        compassActivityDelegate.onSaveInstanceState(outState);
    }
}
