
package com.google.code.geobeagle.di;

import com.google.code.geobeagle.Action;
import com.google.code.geobeagle.DeleteAction;
import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ViewAction;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.GeocacheFromMyLocationFactory;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.data.di.GeocacheFactory;
import com.google.code.geobeagle.data.di.GeocacheVectorFactory;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;
import com.google.code.geobeagle.io.LocationSaver;
import com.google.code.geobeagle.io.di.DatabaseDI;
import com.google.code.geobeagle.io.di.GpxImporterDI;
import com.google.code.geobeagle.io.di.DatabaseDI.SQLiteWrapper;
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
        final LocationControl locationControl = LocationControl.create(locationManager);
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
