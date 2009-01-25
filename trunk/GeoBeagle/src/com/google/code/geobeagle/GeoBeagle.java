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

import com.google.code.geobeagle.LocationControl.LocationChooser;
import com.google.code.geobeagle.intents.DestinationToCachePage;
import com.google.code.geobeagle.intents.DestinationToGoogleMap;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentStarterLocation;
import com.google.code.geobeagle.intents.IntentStarterRadar;
import com.google.code.geobeagle.intents.IntentStarterViewUri;
import com.google.code.geobeagle.ui.CachePageButtonEnabler;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.DestinationListOnClickListener;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.LocationOnKeyListener;
import com.google.code.geobeagle.ui.LocationSetter;
import com.google.code.geobeagle.ui.LocationSetterLifecycleManager;
import com.google.code.geobeagle.ui.LocationViewer;
import com.google.code.geobeagle.ui.MockableContext;
import com.google.code.geobeagle.ui.MockableEditText;
import com.google.code.geobeagle.ui.MockableTextView;
import com.google.code.geobeagle.ui.MyLocationProvider;
import com.google.code.geobeagle.ui.OnCacheButtonClickListenerBuilder;
import com.google.code.geobeagle.ui.OnContentProviderSelectedListener;
import com.google.code.geobeagle.ui.TooString;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends Activity {
    private final ErrorDisplayer mErrorDisplayer;
    private LocationControl mGpsControl;
    private GeoBeagleLocationListener mLocationListener;
    private AppLifecycleManager mAppLifecycleManager;
    private final LocationChooser mLocationChooser;
    private LocationSetter mLocationSetter;
    private LocationViewer mLocationViewer;
    private final ResourceProvider mResourceProvider;
    private ContentSelector mContentSelector;

    public GeoBeagle() {
        super();
        mErrorDisplayer = new ErrorDisplayer(this);
        mLocationChooser = new LocationChooser();
        mResourceProvider = new ResourceProvider(this);
    }

    private void getCoordinatesFromIntent(LocationSetter locationSetter, Intent intent,
            ErrorDisplayer errorDisplayer) {
        try {
            if (intent.getType() == null) {
                final String query = intent.getData().getQuery();
                final CharSequence sanitizedQuery = Util.parseHttpUri(query,
                        new UrlQuerySanitizer(), UrlQuerySanitizer
                                .getAllButNulAndAngleBracketsLegal());
                final CharSequence[] latlon = Util.splitLatLonDescription(sanitizedQuery);
                locationSetter.setLocation(Util.parseCoordinate(latlon[0]), Util
                        .parseCoordinate(latlon[1]), latlon[2]);
            }
        } catch (final Exception e) {
            errorDisplayer.displayError("Error: " + e.getMessage());
        }
    }

    private boolean maybeGetCoordinatesFromIntent() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if ((action != null) && action.equals(Intent.ACTION_VIEW)) {
            getCoordinatesFromIntent(mLocationSetter, intent, mErrorDisplayer);
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            mContentSelector = new ContentSelector((Spinner)findViewById(R.id.content_provider),
                    getPreferences(MODE_PRIVATE));

            final EditText txtLocation = (EditText)findViewById(R.id.go_to);
            final CachePageButtonEnabler cachePageButtonEnabler = new CachePageButtonEnabler(
                    new TooString(txtLocation), findViewById(R.id.cache_page), mResourceProvider);
            txtLocation.setOnKeyListener(new LocationOnKeyListener(cachePageButtonEnabler));

            mLocationViewer = new LocationViewer(new MockableContext(this), new MockableTextView(
                    (TextView)findViewById(R.id.location_viewer)), new MockableTextView(
                    (TextView)findViewById(R.id.last_updated)), new MockableTextView(
                    (TextView)findViewById(R.id.status)));
            final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            mGpsControl = new LocationControl(locationManager, mLocationChooser);
            mLocationListener = new GeoBeagleLocationListener(mGpsControl, mLocationViewer);
            mLocationSetter = new LocationSetter(this, new MockableEditText(txtLocation),
                    mGpsControl, Destination.getDestinationPatterns(mResourceProvider));

            setCacheClickListeners();
            final Button btnGoToList = (Button)findViewById(R.id.go_to_list);
            btnGoToList.setOnClickListener(new DestinationListOnClickListener(mLocationSetter
                    .getDescriptionsAndLocations(), mLocationSetter, new AlertDialog.Builder(this),
                    mErrorDisplayer, cachePageButtonEnabler));

            mAppLifecycleManager = new AppLifecycleManager(getPreferences(MODE_PRIVATE),
                    new LifecycleManager[] {
                            new LocationLifecycleManager(mLocationListener, locationManager),
                            mContentSelector,
                            new LocationSetterLifecycleManager(mLocationSetter,
                                    getString(R.string.initial_destination))
                    });

            ((Spinner)this.findViewById(R.id.content_provider))
                    .setOnItemSelectedListener(new OnContentProviderSelectedListener(
                            mResourceProvider, new MockableTextView(
                                    (TextView)findViewById(R.id.select_cache_prompt)),
                            new MockableTextView((TextView)findViewById(R.id.go_to_cache_prompt))));
        } catch (final Exception e) {
            mErrorDisplayer.displayError(e.toString() + "\n" + Util.getStackTrace(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppLifecycleManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppLifecycleManager.onResume(mErrorDisplayer);
        maybeGetCoordinatesFromIntent();
        final Location location = mGpsControl.getLocation();
        if (location != null)
            mLocationListener.onLocationChanged(location);
    }

    private void setCacheClickListeners() {
        IntentFactory intentFactory = new IntentFactory(new UriParser());
        GetCoordsToast getCoordsToast = new GetCoordsToast(this);
        MyLocationProvider myLocationProvider = new MyLocationProvider(mGpsControl, mErrorDisplayer);

        OnCacheButtonClickListenerBuilder cacheClickListenerSetter = new OnCacheButtonClickListenerBuilder(
                this, mErrorDisplayer);

        cacheClickListenerSetter.set(R.id.object_map, new IntentStarterLocation(this,
                mResourceProvider, intentFactory, myLocationProvider, mContentSelector,
                R.array.map_objects, getCoordsToast), "");
        cacheClickListenerSetter.set(R.id.nearest_objects, new IntentStarterLocation(this,
                mResourceProvider, intentFactory, myLocationProvider, mContentSelector,
                R.array.nearest_objects, getCoordsToast), "");

        cacheClickListenerSetter.set(R.id.maps, new IntentStarterViewUri(this, intentFactory,
                mLocationSetter, new DestinationToGoogleMap(mResourceProvider)), "");
        cacheClickListenerSetter.set(R.id.cache_page, new IntentStarterViewUri(this, intentFactory,
                mLocationSetter, new DestinationToCachePage(mResourceProvider, mContentSelector)),
                "");
        cacheClickListenerSetter.set(R.id.radar, new IntentStarterRadar(this, intentFactory,
                mLocationSetter), "\nPlease install the Radar application to use Radar.");
    }
}
