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
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.Destination;
import com.google.code.geobeagle.data.DestinationVector;
import com.google.code.geobeagle.data.DestinationVectors;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.io.DatabaseFactory;
import com.google.code.geobeagle.io.LocationBookmarksSql;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.regex.Pattern;
import com.google.code.geobeagle.R;

public class CacheList extends ListActivity {

    public static final String SELECT_CACHE = CacheListDelegate.SELECT_CACHE;

    private static CacheListDelegate buildCacheListDelegate(ListActivity listActivity) {
        final DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        final DatabaseFactory.SQLiteWrapper sqliteWrapper = new DatabaseFactory.SQLiteWrapper();
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(listActivity);
        final DatabaseFactory databaseFactory = new DatabaseFactory(sqliteWrapper);
        final LocationBookmarksSql locationBookmarks = new LocationBookmarksSql(
                descriptionsAndLocations, databaseFactory, errorDisplayer);
        final ResourceProvider resourceProvider = new ResourceProvider(listActivity);
        final Pattern[] destinationPatterns = Destination.getDestinationPatterns(resourceProvider);
        final Destination.DestinationFactory destinationFactory = new Destination.DestinationFactory(
                destinationPatterns);
        final DistanceFormatter distanceFormatter = new DistanceFormatter();
        final DestinationVector.DestinationVectorFactory destinationVectorFactory = new DestinationVector.DestinationVectorFactory(
                destinationFactory, listActivity.getString(R.string.my_current_location),
                distanceFormatter);
        final DestinationVector.LocationComparator locationComparator = new DestinationVector.LocationComparator();
        final DestinationVectors destinationVectors = new DestinationVectors(locationComparator,
                destinationVectorFactory);
        final LocationChooser locationChooser = new LocationChooser();
        final LocationControl locationControl = new LocationControl(((LocationManager)listActivity
                .getSystemService(Context.LOCATION_SERVICE)), locationChooser);
        final CacheListDelegate.CacheListDelegateFactory cacheListDelegateFactory = new CacheListDelegate.CacheListDelegateFactory();
        final CacheListData cacheListData = new CacheListData(destinationVectors,
                destinationVectorFactory);
        final Intent intent = new Intent(listActivity, GeoBeagle.class);
        return new CacheListDelegate(listActivity, locationBookmarks, locationControl,
                cacheListDelegateFactory, cacheListData, intent);
    }

    private CacheListDelegate mCacheListDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCacheListDelegate = buildCacheListDelegate(this);
        mCacheListDelegate.onCreate();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCacheListDelegate.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCacheListDelegate.onResume();
    }

}
