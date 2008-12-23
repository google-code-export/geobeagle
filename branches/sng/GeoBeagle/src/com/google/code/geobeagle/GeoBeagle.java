
package com.google.code.geobeagle;

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
    private AlertDialog mDlgError;
    private GpsLocationListener mGpsLocationListener;
    private LocationSetter mLocationSetter;
    private LocationViewer mLocationViewer;
    private ErrorDisplayer mErrorDisplayer;
    private LifecycleManager mLifecycleManager;
    private GpsControl mGpsControl;
    private final IntentFactoryImpl intentFactory = new IntentFactoryImpl(new UriParserImpl());
    private final ActivityStarterImpl activityStarter = new ActivityStarterImpl(this);

    private AlertDialog createErrorDialog() {
        return new AlertDialog.Builder(this).setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                }).create();
    }

    private void getCoordinatesFromIntent(LocationSetter locationSetter, Intent intent,
            AlertDialog alertDialog) {
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
            alertDialog.setMessage("Error: " + e.getMessage());
            alertDialog.show();
            startActivity(new Intent(Intent.ACTION_VIEW, intent.getData()));
        }
    }

    private boolean maybeGetCoordinatesFromIntent() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if ((action != null) && action.equals(Intent.ACTION_VIEW)) {
            getCoordinatesFromIntent(mLocationSetter, intent, mDlgError);
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
            txtLocation.setOnKeyListener(new LocationOnKeyListener(
                    (Button)findViewById(R.id.cache_page), new TooString(txtLocation)));

            mDlgError = createErrorDialog();
            mLocationViewer = new LocationViewerImpl(new MockableContext(this),
                    new MockableTextView((TextView)findViewById(R.id.location_viewer)),
                    new MockableTextView((TextView)findViewById(R.id.last_updated)),
                    new MockableTextView((TextView)findViewById(R.id.status)));
            mGpsLocationListener = new GpsLocationListener(mLocationViewer);
            mGpsControl = new GpsControlImpl(
                    (LocationManager)getSystemService(Context.LOCATION_SERVICE),
                    mGpsLocationListener);
            mLocationSetter = new LocationSetterImpl(this, new MockableEditText(txtLocation),
                    mGpsControl);

            setOnClickListeners(mLocationSetter);
            final Button btnGoToList = (Button)findViewById(R.id.go_to_list);
            mErrorDisplayer = new ErrorDisplayerImpl(this);
            btnGoToList.setOnClickListener(new DestinationListOnClickListener(mLocationSetter
                    .getDescriptionsAndLocations(), mLocationSetter, new AlertDialog.Builder(this),
                    mErrorDisplayer));

            mLifecycleManager = new LifecycleManagerImpl(mGpsControl, mLocationSetter,
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
        if (!maybeGetCoordinatesFromIntent()) {
            final Location location = mGpsControl.getLocation();
            if (location != null)
                mGpsLocationListener.onLocationChanged(location);
        }
    }

    private void setOnClickListener(int id, IntentStarter intentStarter) {
        ((Button)findViewById(id)).setOnClickListener(new OnActivityButtonLinkClickListener(
                intentFactory, intentStarter, activityStarter, mLocationSetter, mDlgError));
    }

    private void setOnClickListeners(final LocationSetter controls) {
        final GetCoordsToast getCoordsToast = new GetCoordsToastImpl(this);
        final ResourceProvider resourceProvider = new ResourceProviderImpl(this);

        setOnClickListener(R.id.radar, new RadarIntentStarter());
        setOnClickListener(R.id.geocaching_map, new GeocachingMapsIntentStarter(getCoordsToast,
                resourceProvider));
        setOnClickListener(R.id.nearest_caches, new NearestCachesIntentStarter(getCoordsToast,
                resourceProvider));
        setOnClickListener(R.id.maps, new MapsIntentStarter(resourceProvider));
        setOnClickListener(R.id.cache_page, new CachePageIntentStarter(resourceProvider));
    }
}
