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

package com.google.code.geobeagle.activity.map;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.map.GeoMapActivityDelegate.MenuActionToggleSatellite;
import com.google.code.geobeagle.activity.map.QueryManager.CachedNeedsLoading;
import com.google.code.geobeagle.activity.map.QueryManager.Loader;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.activity.map.QueryManager.ToasterTooManyCaches;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class GeoMapActivityModule extends AbstractAndroidModule {

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface GeoMapActivity {
    }

    @Override
    protected void configure() {
        bind(GeoMapMenuActionsFactory.class)
                .toProvider(
                        FactoryProvider.newFactory(GeoMapMenuActionsFactory.class,
                                GeoMapMenuActions.class));
        bind(GeoPoint.class).toInstance(new GeoPoint(0, 0));
    }

    public interface GeoMapMenuActionsFactory {
        public GeoMapMenuActions create(GeoMapView geoMapView);
    }

    @Provides
    Drawable providesDefaultMarker(Resources resources) {
        return resources.getDrawable(R.drawable.map_pin2_others);
    }

    @Provides
    QueryManager providesQueryManager(LoaderImpl loader, CachedNeedsLoading cachedNeedsLoading) {
        return new QueryManager(loader, cachedNeedsLoading, new int[] {
                0, 0, 0, 0
        });
    }

    @Provides
    @ToasterTooManyCaches
    Toaster toasterProvider(Activity activity) {
        return new Toaster(activity, R.string.too_many_caches, Toast.LENGTH_SHORT);
    }

    public static class GeoMapMenuActions extends MenuActions {

        @Inject
        public GeoMapMenuActions(@Assisted GeoMapView geoMapView, Activity activity,
                Resources resources) {
            super(resources);

            MenuActionToggleSatellite menuActionToggleSatellite = new MenuActionToggleSatellite(
                    geoMapView);
            add(menuActionToggleSatellite);
            add(new MenuActionCacheList(activity));
            add(new GeoMapActivityDelegate.MenuActionCenterLocation(geoMapView.getController(),
                    new FixedMyLocationOverlay(activity, geoMapView)));
        }

    }

}
