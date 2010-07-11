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

import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.presenter.GeoBeaglePackageAnnotations.DifficultyAndTerrainPainterAnnotation;
import com.google.code.geobeagle.activity.map.CachePinsOverlayFactory.CachePinsQueryManager;
import com.google.code.geobeagle.activity.map.QueryManager.CachedNeedsLoading;
import com.google.code.geobeagle.activity.map.QueryManager.LoaderImpl;
import com.google.code.geobeagle.activity.map.QueryManager.PeggedLoader;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.content.Context;
import android.content.res.Resources;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class GeoMapActivityModule extends AbstractAndroidModule {

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface DensityMapQueryManager {}

    static class NullOverlay extends Overlay {
    }

    @Override
    protected void configure() {
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
