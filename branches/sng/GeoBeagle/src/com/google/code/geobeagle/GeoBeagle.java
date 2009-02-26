/*
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
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.Destination.DestinationFactory;
import com.google.code.geobeagle.intents.DestinationToCachePage;
import com.google.code.geobeagle.intents.DestinationToGoogleMap;
import com.google.code.geobeagle.intents.IntentFactory;
import com.google.code.geobeagle.intents.IntentStarterLocation;
import com.google.code.geobeagle.intents.IntentStarterRadar;
import com.google.code.geobeagle.intents.IntentStarterViewUri;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.ui.CachePageButtonEnabler;
import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.DestinationListOnClickListener;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.LocationOnKeyListener;
import com.google.code.geobeagle.ui.LocationSetter;
import com.google.code.geobeagle.ui.LocationViewer;
import com.google.code.geobeagle.ui.MockableEditText;
import com.google.code.geobeagle.ui.MockableTextView;
import com.google.code.geobeagle.ui.MyLocationProvider;
import com.google.code.geobeagle.ui.OnCacheButtonClickListenerBuilder;
import com.google.code.geobeagle.ui.OnContentProviderSelectedListener;
import com.google.code.geobeagle.ui.TooString;
import com.google.code.geobeagle.ui.LocationViewer.MeterFormatter;
import com.google.code.geobeagle.ui.LocationViewer.MeterView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Pattern;

/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends Activity {
    private CachePageButtonEnabler mCachePageButtonEnabler;
    private ContentSelector mContentSelector;
    private final ErrorDisplayer mErrorDisplayer;
    private LocationControl mGpsControl;
    private final Handler mHandler;
    private GeoBeagleLocationListener mLocationListener;
    private LocationSetter mLocationSetter;
    private LocationViewer mLocationViewer;
    private final ResourceProvider mResourceProvider;

    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {
            mLocationViewer.refreshLocation();
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    };
    private GeoBeagleDelegate mGeoBeagleDelegate;

    public GeoBeagle() {
        super();
        mErrorDisplayer = new ErrorDisplayer(this);
        mResourceProvider = new ResourceProvider(this);
        mHandler = new Handler();
    }

    private MockableTextView createTextView(int id) {
        return new MockableTextView((TextView)findViewById(id));
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
        if (action != null) {
            if (action.equals(Intent.ACTION_VIEW)) {
                getCoordinatesFromIntent(mLocationSetter, intent, mErrorDisplayer);
                return true;
            } else if (action.equals(CacheList.SELECT_CACHE)) {
                mLocationSetter.setLocation(intent.getStringExtra("location"));
                mCachePageButtonEnabler.check();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            GeoBeagleBuilder builder = new GeoBeagleBuilder(this);
            mContentSelector = builder.createContentSelector(getPreferences(Activity.MODE_PRIVATE));

            final EditText txtLocation = (EditText)findViewById(R.id.go_to);
            mCachePageButtonEnabler = CachePageButtonEnabler.create(new TooString(txtLocation),
                    findViewById(R.id.cache_page), findViewById(R.id.cache_details),
                    mResourceProvider);
            txtLocation.setOnKeyListener(new LocationOnKeyListener(mCachePageButtonEnabler));

            mLocationViewer = new LocationViewer(mResourceProvider, new MeterView(
                    createTextView(R.id.location_viewer), new MeterFormatter()),
                    createTextView(R.id.provider), createTextView(R.id.lag),
                    createTextView(R.id.accuracy), createTextView(R.id.status),
                    new LocationViewer.Time(), new Location(""));
            final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            mGpsControl = new LocationControl(locationManager, new LocationChooser());
            mLocationListener = new GeoBeagleLocationListener(mGpsControl, mLocationViewer);
            DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
            final Pattern[] destinationPatterns = Destination
                    .getDestinationPatterns(mResourceProvider);
            final DestinationFactory destinationFactory = new DestinationFactory(
                    destinationPatterns);
            final Database database = Database.create(this);
            LocationBookmarksSql locationBookmarks = new LocationBookmarksSql(
                    descriptionsAndLocations, database, destinationFactory, mErrorDisplayer);

            MockableEditText mockableTxtLocation = new MockableEditText(txtLocation);
            LocationSaver locationSaver = new LocationSaver(database, destinationFactory,
                    mErrorDisplayer);
            mockableTxtLocation
                    .setOnFocusChangeListener(new LocationSetter.EditTextFocusChangeListener(
                            locationSaver, mockableTxtLocation));
            final String initialDestination = getString(R.string.initial_destination);
            mLocationSetter = new LocationSetter(this, mockableTxtLocation, mGpsControl,
                    destinationPatterns, initialDestination, mErrorDisplayer, locationSaver);

            setCacheClickListeners();

            ((Button)findViewById(R.id.go_to_list))
                    .setOnClickListener(new DestinationListOnClickListener(this));

            AppLifecycleManager appLifecycleManager = new AppLifecycleManager(
                    getPreferences(MODE_PRIVATE), new LifecycleManager[] {
                            mLocationSetter, locationBookmarks,
                            new LocationLifecycleManager(mLocationListener, locationManager),
                            mContentSelector
                    });
            mGeoBeagleDelegate = GeoBeagleDelegate.buildGeoBeagleDelegate(this,
                    appLifecycleManager, mLocationSetter, mErrorDisplayer);
            mGeoBeagleDelegate.onCreate();

            ((Spinner)this.findViewById(R.id.content_provider))
                    .setOnItemSelectedListener(new OnContentProviderSelectedListener(
                            mResourceProvider, new MockableTextView(
                                    (TextView)findViewById(R.id.select_cache_prompt)),
                            new MockableTextView((TextView)findViewById(R.id.go_to_cache_prompt))));

            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 1000);
        } catch (final Exception e) {
            mErrorDisplayer.displayErrorAndStack(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGeoBeagleDelegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGeoBeagleDelegate.onResume();
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
