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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.GeocacheFactory;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.data.GeocacheVectorFactory;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.DatabaseDI;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;
import com.google.code.geobeagle.io.GpxImporterDI;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.GeocacheListAdapter;
import com.google.code.geobeagle.ui.GeocacheRowInflater;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.LayoutInflater;

public class CacheListDelegateDI {

    public static CacheListDelegate create(ListActivity parent, LayoutInflater layoutInflater) {
        final ErrorDisplayer errorDisplayer = new ErrorDisplayer(parent);
        final Database database = DatabaseDI.create(parent);
        final ResourceProvider resourceProvider = new ResourceProvider(parent);
        final LocationManager locationManager = (LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE);
        final LocationControl locationControl = LocationControlDi.create(locationManager);
        final GeocacheFactory geocacheFactory = new GeocacheFactory();
        final GeocacheFromMyLocationFactory geocacheFromMyLocationFactory = new GeocacheFromMyLocationFactory(
                geocacheFactory, locationControl, errorDisplayer);
        final GeocachesSql locationBookmarks = DatabaseDI.create(locationControl, database);
        final SQLiteWrapper sqliteWrapper = new SQLiteWrapper(null);
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final DistanceFormatter distanceFormatter = new DistanceFormatter();
        final LocationSaver locationSaver = new LocationSaver(database, sqliteWrapper, cacheWriter);
        final GeocacheVectorFactory geocacheVectorFactory = new GeocacheVectorFactory(
                geocacheFromMyLocationFactory, locationSaver, distanceFormatter, resourceProvider);
        final LocationComparator locationComparator = new LocationComparator();
        final GeocacheVectors geocacheVectors = new GeocacheVectors(locationComparator,
                geocacheVectorFactory);
        final CacheListData cacheListData = new CacheListData(geocacheVectors,
                geocacheVectorFactory);
        final Action actions[] = CacheListDelegateDI.create(parent, database, sqliteWrapper,
                cacheListData, cacheWriter, geocacheVectors, errorDisplayer);
        final CacheListDelegate.CacheListOnCreateContextMenuListener.Factory factory = new CacheListDelegate.CacheListOnCreateContextMenuListener.Factory();
        final GpxImporter gpxImporter = GpxImporterDI.create(database, sqliteWrapper,
                errorDisplayer, parent);

        final GeocacheRowInflater geocacheRowInflater = new GeocacheRowInflater(layoutInflater);
        final GeocacheListAdapter geocacheListAdapter = new GeocacheListAdapter(geocacheVectors,
                geocacheRowInflater);

        return new CacheListDelegate(parent, locationBookmarks, locationControl, cacheListData,
                geocacheVectors, geocacheListAdapter, errorDisplayer, actions, factory, gpxImporter);
    }

    public static Action[] create(ListActivity parent, Database database,
            SQLiteWrapper sqliteWrapper, CacheListData cacheListData, CacheWriter cacheWriter,
            GeocacheVectors geocacheVectors, ErrorDisplayer errorDisplayer) {
        final Intent intent = new Intent(parent, GeoBeagle.class);
        final ViewAction viewAction = new ViewAction(geocacheVectors, parent, intent);
        final DeleteAction deleteAction = new DeleteAction(database, sqliteWrapper, cacheWriter,
                geocacheVectors, errorDisplayer);
        return new Action[] {
                deleteAction, viewAction
        };
    }

}
