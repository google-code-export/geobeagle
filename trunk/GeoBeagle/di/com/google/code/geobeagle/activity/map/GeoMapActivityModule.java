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
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.DifficultyAndTerrainPainterAnnotation;
import com.google.code.geobeagle.activity.map.CachePinsOverlayFactory.CachePinsQueryManager;
import com.google.code.geobeagle.activity.map.DensityOverlayDelegate.DensityOverlayPaint;
import com.google.code.geobeagle.activity.map.GeoMapActivityDelegate.MenuActionCenterLocation;
import com.google.code.geobeagle.activity.map.GeoMapActivityDelegate.MenuActionToggleSatellite;
import com.google.code.geobeagle.activity.map.QueryManager.CachedNeedsLoading;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.activity.map.QueryManager.PeggedLoader;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class GeoMapActivityModule extends AbstractAndroidModule {

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface DensityMapQueryManager {}
    
    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface GeoMapActivityMenuActions {}

    static class NullOverlay extends Overlay {
    }

    @Override
    protected void configure() {
    }

    @Provides
    GeoPoint geoPointProvider()
    {
        return new GeoPoint(0, 0);
    }
    
    @Provides
    @DifficultyAndTerrainPainterAnnotation
    CacheItemFactory providesCacheItemDifficultyAndTerrainFactory(
            @DifficultyAndTerrainPainterAnnotation IconRenderer iconRenderer,
            MapViewBitmapCopier mapViewBitmapCopier, IconOverlayFactory iconOverlayFactory) {
        return new CacheItemFactory(iconRenderer, mapViewBitmapCopier, iconOverlayFactory);
    }

    @Provides
    CachePinsOverlay providesCachePinsOverlay(CacheItemFactory cacheItemFactory, Context context,
            Resources resources) {
        return new CachePinsOverlay(resources, cacheItemFactory, context, new ArrayList<Geocache>());
    }
    
    @Provides
    @DensityOverlayPaint
    Paint providesDensityOverlayPaint()
    {
        Paint paint = new Paint();
        paint.setARGB(128, 255, 0, 0);
        return paint;
    }

    @Provides
    @GeoMapActivityMenuActions
    MenuActions providesGeoMapMenuActions(Activity activity, Resources resources) {
        final MenuActions menuActions = new MenuActions(resources);
        final GeoMapView geoMapView = (GeoMapView)activity.findViewById(R.id.mapview);
        menuActions.add(new MenuActionToggleSatellite(geoMapView));
        menuActions.add(new MenuActionCacheList(activity));
        final MyLocationOverlay fixedMyLocationOverlay = ((GeoMapActivity)activity)
                .getMyLocationOverlay();
        menuActions.add(new MenuActionCenterLocation(geoMapView.getController(),
                fixedMyLocationOverlay));
        return menuActions;
    }

    @Provides
    @CachePinsQueryManager
    QueryManager providesQueryManagerCachePins(LoaderImpl loaderImpl, CachedNeedsLoading cachedNeedsLoading) {
        return new QueryManager(loaderImpl, cachedNeedsLoading, new int[] {
                0, 0, 0, 0
        });
    }

    @Provides
    @DensityMapQueryManager
    QueryManager providesQueryManagerDensityMap(PeggedLoader peggedLoader, CachedNeedsLoading cachedNeedsLoading) {
        return new QueryManager(peggedLoader, cachedNeedsLoading, new int[] {
                0, 0, 0, 0
        });
    }
}
