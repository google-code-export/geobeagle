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

package com.google.code.geobeagle.activity.main;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Refresher;
import com.google.code.geobeagle.CompassListener.Azimuth;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.main.intents.GeocacheToCachePage;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleMap;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo.IntentStarterMap;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo.IntentStarterRadar;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo.ShowMapIntent;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo.ShowRadarIntent;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionGoogleMaps;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.PawImages;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.ResourceImages;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.RibbonImages;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.code.geobeagle.activity.map.GeoMapActivity;
import com.google.code.geobeagle.activity.searchonline.NullRefresher;
import com.google.code.geobeagle.location.LocationLifecycleManager;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SystemServiceProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;

public class GeoBeagleModule extends AbstractAndroidModule {

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface DefaultSharedPreferences {}

    static final class DefaultSharedPreferencesProvider implements Provider<SharedPreferences> {
        private final Context mContext;

        @Inject
        DefaultSharedPreferencesProvider(Context context) {
            mContext = context;
        }

        @Override
        public SharedPreferences get() {
            return PreferenceManager.getDefaultSharedPreferences(mContext);
        }
    }

    static class GeoMapIntentProvider implements Provider<Intent> {
        private final Activity activity;

        @Inject
        public GeoMapIntentProvider(Activity activity) {
            this.activity = activity;
        }

        @Override
        public Intent get() {
            return new Intent(activity, GeoMapActivity.class);
        }
    }
    
    static final class IntentStarterMapProvider implements Provider<IntentStarterGeo> {
        private final GeoBeagle geoBeagle;
        private final Intent intent;

        @Inject
        IntentStarterMapProvider(GeoBeagle geoBeagle, @ShowMapIntent Intent intent) {
            this.geoBeagle = geoBeagle;
            this.intent = intent;
        }

        @Override
        public IntentStarterGeo get() {
            return new IntentStarterGeo(geoBeagle, intent);
        }
    }

    static final class IntentStarterRadarProvider implements Provider<IntentStarterGeo> {
        private final GeoBeagle geoBeagle;
        private final Intent intent;

        @Inject
        IntentStarterRadarProvider(GeoBeagle geoBeagle, @ShowRadarIntent Intent intent) {
            this.geoBeagle = geoBeagle;
            this.intent = intent;
        }

        @Override
        public IntentStarterGeo get() {
            return new IntentStarterGeo(geoBeagle, intent);
        }
    }

    
    @Override
    protected void configure() {
        bind(Refresher.class).to(NullRefresher.class);
        bind(SensorManager.class).toProvider(
                new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
        bind(Intent.class).annotatedWith(ShowRadarIntent.class).toInstance(
                new Intent("com.google.android.radar.SHOW_RADAR"));
        bind(Intent.class).annotatedWith(ShowMapIntent.class)
                .toProvider(GeoMapIntentProvider.class);
        bind(String.class).annotatedWith(Names.named("OpenMapError")).toInstance("Map error");
        bind(String.class).annotatedWith(Names.named("OpenUriError")).toInstance("");
        bindConstant().annotatedWith(Azimuth.class).to(-1440f);
        bind(IntentStarterGeo.class).annotatedWith(IntentStarterMap.class).toProvider(
                IntentStarterMapProvider.class);
        bind(IntentStarterGeo.class).annotatedWith(IntentStarterRadar.class).toProvider(
                IntentStarterRadarProvider.class);
        bind(SharedPreferences.class).annotatedWith(DefaultSharedPreferences.class).toProvider(
                DefaultSharedPreferencesProvider.class);
    }

    private UnlabelledAttributeViewer getImagesOnDifficulty(GraphicsGenerator graphicsGenerator,
            final Drawable[] pawDrawables, ImageView imageView) {
        return new UnlabelledAttributeViewer(imageView, graphicsGenerator.getRatings(pawDrawables,
                10));
    }

    @Provides
    AlertDialog.Builder providesAlertDialogBuilder(Activity activity) {
        return new AlertDialog.Builder(activity);
    }

    @Provides
    AppLifecycleManager providesAppLifecycleManager(
            @DefaultSharedPreferences SharedPreferences sharedPreferences, RadarView radarView,
            LocationControlBuffered locationControlBuffered,
            LocationManager locationManager) {
        return new AppLifecycleManager(sharedPreferences, new LifecycleManager[] {
                new LocationLifecycleManager(locationControlBuffered, locationManager),
                new LocationLifecycleManager(radarView, locationManager)
        });
    }

    @Provides
    CacheDetailsOnClickListener providesCacheDetailsOnClickListener(Activity activity,
            AlertDialog.Builder cacheDetailsBuilder, LayoutInflater layoutInflater) {
        return Misc.createCacheDetailsOnClickListener((GeoBeagle)activity, cacheDetailsBuilder,
                layoutInflater);
    }

    @Provides
    @Named("GeocacheIcon")
    ImageView providesGCIcon(Activity activity) {
        return (ImageView)activity.findViewById(R.id.gcicon);
    }

    @Provides
    @Named("GeocacheId")
    TextView providesGcId(Activity activity) {
        return (TextView)activity.findViewById(R.id.gcid);
    }

    @Provides
    @Named("GeocacheName")
    TextView providesGCName(Activity activity) {
        return (TextView)activity.findViewById(R.id.gcname);
    }

    @Provides
    GeoBeagle providesGeoBeagle(Activity activity) {
        return (GeoBeagle)activity;
    }

    @Provides
    @Named("GeocacheDifficulty")
    AttributeViewer providesGeocacheDifficulty(Activity activity,
            @RibbonImages UnlabelledAttributeViewer ribbonImages) {
        return new LabelledAttributeViewer(
                (TextView)activity.findViewById(R.id.gc_text_difficulty), ribbonImages);
    }

    @Provides
    @Named("GeocacheTerrain")
    AttributeViewer providesGeocacheTerrain(Activity activity,
            @PawImages UnlabelledAttributeViewer pawImages) {
        return new LabelledAttributeViewer((TextView)activity.findViewById(R.id.gc_text_terrain),
                pawImages);
    }

    @Provides
    @Named("ImageViewDifficulty")
    ImageView providesImageViewDifficulty(GeoBeagle geoBeagle) {
        return (ImageView)geoBeagle.findViewById(R.id.gc_difficulty);
    }

    @Provides
    @Named("ImageViewTerrain")
    ImageView providesImageViewTerrain(GeoBeagle geoBeagle) {
        return (ImageView)geoBeagle.findViewById(R.id.gc_terrain);
    }

    @Provides
    IntentStarterGeo providesIntentStarterRadar(GeoBeagle geoBeagle, @ShowRadarIntent Intent intent) {
        return new IntentStarterGeo(geoBeagle, intent);
    }

    @Provides
    @Named("IntentStarterViewCachePage")
    IntentStarterViewUri providesIntentStarterViewCachePage(GeoBeagle geoBeagle,
            IntentFactory intentFactory, GeocacheToCachePage geocacheToCachePage) {
        return new IntentStarterViewUri(geoBeagle, intentFactory, geocacheToCachePage);
    }

    @Provides
    @Named("IntentStarterViewGoogleMap")
    IntentStarterViewUri providesIntentStarterViewGoogleMap(GeoBeagle geoBeagle,
            IntentFactory intentFactory, GeocacheToGoogleMap geocacheToGoogleMap) {
        return new IntentStarterViewUri(geoBeagle, intentFactory, geocacheToGoogleMap);
    }

    @Provides
    MenuActions providesMenuActions(GeoBeagle geoBeagle, Resources resources,
            @Named("IntentStarterViewGoogleMap") IntentStarterViewUri viewGoogleMapIntentStarter) {
        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(geoBeagle), new MenuActionEditGeocache(geoBeagle),
                // new MenuActionLogDnf(this), new MenuActionLogFind(this),
                new MenuActionSearchOnline(geoBeagle), new MenuActionSettings(geoBeagle),
                new MenuActionGoogleMaps(viewGoogleMapIntentStarter)
        };
        return new MenuActions(resources, menuActionArray);
    }

    @Provides
    OnClickListener providesOnClickListener(@SuppressWarnings("unused") Activity activity) {
        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
    }

    @Provides
    @PawImages
    UnlabelledAttributeViewer providesPawImagesOnDifficulty(Resources resources,
            GraphicsGenerator graphicsGenerator, @Named("ImageViewTerrain") ImageView imageView) {
        final Drawable[] pawDrawables = {
                resources.getDrawable(R.drawable.paw_unselected_dark),
                resources.getDrawable(R.drawable.paw_half_light),
                resources.getDrawable(R.drawable.paw_selected_light)
        };
        return getImagesOnDifficulty(graphicsGenerator, pawDrawables, imageView);
    }

    @Provides
    RadarView providesRadarView(Activity activity) {
        RadarView radarView = (RadarView)activity.findViewById(R.id.radarview);
        radarView.setUseImperial(false);
        radarView.setDistanceView((TextView)activity.findViewById(R.id.radar_distance),
                (TextView)activity.findViewById(R.id.radar_bearing), (TextView)activity
                        .findViewById(R.id.radar_accuracy));
        return radarView;
    }

    @Provides
    ResourceImages providesResourceImages(Activity activity) {
        return new ResourceImages((ImageView)activity.findViewById(R.id.gccontainer), Arrays
                .asList(GeocacheViewer.CONTAINER_IMAGES));
    }

    @Provides
    @RibbonImages
    UnlabelledAttributeViewer providesRibbonImagesOnDifficulty(Resources resources,
            GraphicsGenerator graphicsGenerator, @Named("ImageViewDifficulty") ImageView imageView) {
        final Drawable[] ribbonDrawables = {
                resources.getDrawable(R.drawable.ribbon_unselected_dark),
                resources.getDrawable(R.drawable.ribbon_half_bright),
                resources.getDrawable(R.drawable.ribbon_selected_bright)
        };
        return getImagesOnDifficulty(graphicsGenerator, ribbonDrawables, imageView);
    }

    @Provides
    WebPageAndDetailsButtonEnabler providesWebPageAndDetailsButtonEnabler(Activity geoBeagle) {
        return Misc.create((GeoBeagle)geoBeagle, geoBeagle.findViewById(R.id.cache_page), geoBeagle
                .findViewById(R.id.cache_details));
    }
}
