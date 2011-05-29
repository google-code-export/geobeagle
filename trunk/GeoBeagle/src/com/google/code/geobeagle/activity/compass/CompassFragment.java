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

import com.google.code.geobeagle.CompassListener;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListActivityHoneycomb;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.shakewaker.ShakeWaker;
import com.google.inject.Injector;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CompassFragment extends Fragment {
    private Geocache geocache;
    private GeoBeagleSensors geoBeagleSensors;

    public Geocache getGeocache() {
        return geocache;
    }

    public GeoBeagleSensors getGeoBeagleSensors() {
        return geoBeagleSensors;
    }

    @Override
    public void onResume() {
        super.onResume();
        geoBeagleSensors.registerSensors();
    }

    @Override
    public void onPause() {
        super.onPause();
        geoBeagleSensors.unregisterSensors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.compass, container, false);
        Bundle arguments = getArguments();
        CacheListActivityHoneycomb cacheListActivity = (CacheListActivityHoneycomb)getActivity();
        Injector injector = cacheListActivity.getInjector();
        GeocacheViewerFactory geocacheViewerFactory = injector
                .getInstance(GeocacheViewerFactory.class);
        GeocacheViewer geocacheViewer = geocacheViewerFactory.create(new ViewViewContainer(
                inflatedView));
        GeocacheFromParcelFactory geocacheFromParcelFactory = injector
                .getInstance(GeocacheFromParcelFactory.class);
        geocache = geocacheFromParcelFactory.createFromBundle(arguments);
        geocacheViewer.set(geocache);
        injector.getInstance(CompassClickListenerSetter.class).setListeners(
                new ViewViewContainer(inflatedView), cacheListActivity);

        RadarView radarView = (RadarView)inflatedView.findViewById(R.id.radarview);
        geoBeagleSensors = new GeoBeagleSensors(injector.getInstance(SensorManager.class),
                radarView, injector.getInstance(SharedPreferences.class),
                injector.getInstance(CompassListener.class),
                injector.getInstance(ShakeWaker.class),
                injector.getProvider(LocationManager.class),
                injector.getInstance(SatelliteCountListener.class));

        LocationManager locationManager = injector.getInstance(LocationManager.class);
        // Register for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, radarView);
        locationManager
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, radarView);

        return inflatedView;
    }
}
