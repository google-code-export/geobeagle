
package com.google.code.geobeagle.di;

import com.google.code.geobeagle.Action;
import com.google.code.geobeagle.DeleteAction;
import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ViewAction;
import com.google.code.geobeagle.data.CacheListData;
import com.google.code.geobeagle.data.DistanceFormatter;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.data.GeocacheVector.LocationComparator;
import com.google.code.geobeagle.data.di.GeocacheFromTextFactory;
import com.google.code.geobeagle.data.di.GeocacheVectorFactory;
import com.google.code.geobeagle.io.CacheWriter;
import com.google.code.geobeagle.io.Database;
import com.google.code.geobeagle.io.GeocachesSql;
import com.google.code.geobeagle.io.GpxImporter;
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
        final GeocacheFromTextFactory geocacheFromTextFactory = new GeocacheFromTextFactory(
                resourceProvider);
        final LocationControl locationControl = LocationControl.create(((LocationManager)parent
                .getSystemService(Context.LOCATION_SERVICE)));
        final GeocachesSql locationBookmarks = DatabaseDI.create(locationControl, database,
                geocacheFromTextFactory, errorDisplayer);

        final DistanceFormatter distanceFormatter = new DistanceFormatter();
        final GeocacheVectorFactory geocacheVectorFactory = new GeocacheVectorFactory(
                geocacheFromTextFactory, distanceFormatter, resourceProvider);
        final LocationComparator locationComparator = new LocationComparator();
        final GeocacheVectors geocacheVectors = new GeocacheVectors(locationComparator,
                geocacheVectorFactory);
        final CacheListData cacheListData = new CacheListData(geocacheVectors,
                geocacheVectorFactory);
        final DatabaseDI.SQLiteWrapper sqliteWrapper = new DatabaseDI.SQLiteWrapper(null);
        final Action actions[] = CacheListDelegateDI.create(parent, database, sqliteWrapper,
                cacheListData, geocacheVectors, errorDisplayer);
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
            SQLiteWrapper sqliteWrapper, CacheListData cacheListData,
            GeocacheVectors geocacheVectors, ErrorDisplayer errorDisplayer) {
        final Intent intent = new Intent(parent, GeoBeagle.class);
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(sqliteWrapper);
        final ViewAction viewAction = new ViewAction(geocacheVectors, parent, intent);
        final DeleteAction deleteAction = new DeleteAction(database, sqliteWrapper, cacheWriter,
                geocacheVectors, errorDisplayer);
        return new Action[] {
                deleteAction, viewAction
        };
    }

}
