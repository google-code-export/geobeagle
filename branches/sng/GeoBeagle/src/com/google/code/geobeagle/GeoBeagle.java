
package com.google.code.geobeagle;

import com.google.code.geobeagle.intents.ActivityStarter;
import com.google.code.geobeagle.intents.CreateIntentFromDestinationFactory;
import com.google.code.geobeagle.intents.DestinationToCachePageUri;
import com.google.code.geobeagle.intents.DestinationToMapsUri;
import com.google.code.geobeagle.intents.GotoCache;
import com.google.code.geobeagle.intents.GotoCacheByViewingUri;
import com.google.code.geobeagle.intents.GotoCacheRadar;
import com.google.code.geobeagle.intents.IntentFromActionFactory;
import com.google.code.geobeagle.intents.IntentFromActionUriFactory;
import com.google.code.geobeagle.intents.SelectCache;
import com.google.code.geobeagle.ui.CachePageButtonEnabler;
import com.google.code.geobeagle.ui.DestinationListOnClickListener;
import com.google.code.geobeagle.ui.ErrorDialog;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GetCoordsToast;
import com.google.code.geobeagle.ui.LocationOnKeyListener;
import com.google.code.geobeagle.ui.LocationSetter;
import com.google.code.geobeagle.ui.LocationViewer;
import com.google.code.geobeagle.ui.MockableContext;
import com.google.code.geobeagle.ui.MockableEditText;
import com.google.code.geobeagle.ui.MockableTextView;
import com.google.code.geobeagle.ui.MyLocationProvider;
import com.google.code.geobeagle.ui.OnGotoCacheClickListener;
import com.google.code.geobeagle.ui.OnSelectCacheButtonClickListener;
import com.google.code.geobeagle.ui.TooString;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GeoBeagle extends Activity {
    private ErrorDialog mErrorDialog;
    private GpsLocationListener mGpsLocationListener;
    private LocationSetter mLocationSetter;
    private LocationViewer mLocationViewer;
    private ErrorDisplayer mErrorDisplayer;
    private LifecycleManager mLifecycleManager;
    private GpsControl mGpsControl;
    private final ActivityStarter activityStarter = new ActivityStarter(this);

    private ErrorDialog createErrorDialog() {
        return new ErrorDialog(new AlertDialog.Builder(this).setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                }).create(), new ResourceProvider(this));
    }

    private void getCoordinatesFromIntent(LocationSetter locationSetter, Intent intent,
            ErrorDialog errorDialog) {
        try {
            final String query = intent.getData().getQuery();
            final String sanitizedQuery = Util.parseHttpUri(query, new UrlQuerySanitizer(),
                    UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal());
            final String[] latlon = Util.getLatLonFromQuery(sanitizedQuery);
            locationSetter.setLocation(Util.minutesToDegrees(latlon[0]), Util
                    .minutesToDegrees(latlon[1]), latlon[2]);
            // startActivityForResult(new RadarIntentCreator().createIntent(new
            // LatLong(ll)), -1);
        } catch (final Exception e) {
            errorDialog.show("Error: " + e.getMessage());
            startActivity(new Intent(Intent.ACTION_VIEW, intent.getData()));
        }
    }

    private boolean maybeGetCoordinatesFromIntent() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if ((action != null) && action.equals(Intent.ACTION_VIEW)) {
            getCoordinatesFromIntent(mLocationSetter, intent, mErrorDialog);
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            final EditText txtLocation = (EditText)findViewById(R.id.go_to);
            final CachePageButtonEnabler cachePageButtonEnabler = new CachePageButtonEnabler(
                    new TooString(txtLocation), findViewById(R.id.cache_page));
            txtLocation.setOnKeyListener(new LocationOnKeyListener(cachePageButtonEnabler));

            mErrorDialog = createErrorDialog();
            mLocationViewer = new LocationViewer(new MockableContext(this), new MockableTextView(
                    (TextView)findViewById(R.id.location_viewer)), new MockableTextView(
                    (TextView)findViewById(R.id.last_updated)), new MockableTextView(
                    (TextView)findViewById(R.id.status)));
            mGpsLocationListener = new GpsLocationListener(mLocationViewer);
            mGpsControl = new GpsControl(
                    (LocationManager)getSystemService(Context.LOCATION_SERVICE),
                    mGpsLocationListener);
            mLocationSetter = new LocationSetter(this, new MockableEditText(txtLocation),
                    mGpsControl);

            setOnClickListeners(mLocationSetter);
            final Button btnGoToList = (Button)findViewById(R.id.go_to_list);
            mErrorDisplayer = new ErrorDisplayer(this);
            btnGoToList.setOnClickListener(new DestinationListOnClickListener(mLocationSetter
                    .getDescriptionsAndLocations(), mLocationSetter, new AlertDialog.Builder(this),
                    mErrorDisplayer, cachePageButtonEnabler));

            mLifecycleManager = new LifecycleManager(mGpsControl, mLocationSetter,
                    getPreferences(MODE_PRIVATE));

        } catch (final Exception e) {
            ((TextView)findViewById(R.id.debug)).setText(e.toString() + "\n"
                    + Util.getStackTrace(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLifecycleManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleManager.onResume(mErrorDisplayer, getString(R.string.initial_destination));
        maybeGetCoordinatesFromIntent();
        final Location location = mGpsControl.getLocation();
        if (location != null)
            mGpsLocationListener.onLocationChanged(location);
    }

    private void setGotoCacheClickListener(int id, GotoCache gotoCache) {
        ((Button)findViewById(id)).setOnClickListener(new OnGotoCacheClickListener(gotoCache,
                activityStarter, mLocationSetter, mErrorDialog));
    }

    private void setSelectCacheClickListener(int id, SelectCache selectCache) {
        ((Button)findViewById(id)).setOnClickListener(new OnSelectCacheButtonClickListener(
                selectCache));
    }

    private void setOnClickListeners(final LocationSetter controls) {
        final IntentFromActionUriFactory intentFromActionUriFactory = new IntentFromActionUriFactory(
                new UriParser());
        final ResourceProvider resourceProvider = new ResourceProvider(this);

        setSelectCacheOnClickListeners(intentFromActionUriFactory, resourceProvider);
        setGotoCacheOnClickListeners(intentFromActionUriFactory, resourceProvider);
    }

    private void setGotoCacheOnClickListeners(
            final IntentFromActionUriFactory intentFromActionUriFactory,
            final ResourceProvider resourceProvider) {
        setGotoCacheClickListener(R.id.maps, new GotoCacheByViewingUri(activityStarter,
                new CreateIntentFromDestinationFactory(intentFromActionUriFactory,
                        new DestinationToMapsUri(resourceProvider))));
        setGotoCacheClickListener(R.id.cache_page, new GotoCacheByViewingUri(activityStarter,
                new CreateIntentFromDestinationFactory(intentFromActionUriFactory,
                        new DestinationToCachePageUri(resourceProvider))));

        setGotoCacheClickListener(R.id.radar, new GotoCacheRadar(activityStarter,
                new IntentFromActionFactory()));
    }

    private void setSelectCacheOnClickListeners(
            final IntentFromActionUriFactory intentFromActionUriFactory,
            ResourceProvider resourceProvider) {
        final GetCoordsToast getCoordsToast = new GetCoordsToast(this);
        final MyLocationProvider myLocationProvider = new MyLocationProvider(mGpsControl,
                mErrorDialog);

        setSelectCacheClickListener(R.id.geocaching_map, new SelectCache(myLocationProvider,
                getCoordsToast, activityStarter, intentFromActionUriFactory, resourceProvider,
                R.string.geocaching_maps_url));
        setSelectCacheClickListener(R.id.nearest_caches, new SelectCache(myLocationProvider,
                getCoordsToast, activityStarter, intentFromActionUriFactory, resourceProvider,
                R.string.nearest_caches_url));
    }
}
