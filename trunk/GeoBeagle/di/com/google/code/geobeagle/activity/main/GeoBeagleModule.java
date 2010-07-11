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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.MenuActionEditGeocache;
import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleGeo;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionGoogleMaps;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.ResourceImages;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;

public class GeoBeagleModule extends AbstractAndroidModule {

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface GeoBeagleActivity {
    }

    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface IntentStarterViewGoogleMap {
    }


    @BindingAnnotation
    @Target( {
            FIELD, PARAMETER, METHOD
    })
    @Retention(RUNTIME)
    public static @interface ChooseNavDialog {
    }
    
    @Override
    protected void configure() {
        bind(Paint.class).toInstance(new Paint());
        bind(Rect.class).toInstance(new Rect());
    }

    private UnlabelledAttributeViewer getImagesOnDifficulty(final Drawable[] pawDrawables,
            ImageView imageView, RatingsArray ratingsArray) {
        return new UnlabelledAttributeViewer(imageView, ratingsArray.getRatings(pawDrawables, 10));
    }

    @Provides
    GeoBeagle providesGeoBeagle(Activity activity) {
        return (GeoBeagle)activity;
    }

    @Provides
    @GeoBeagleActivity
    MenuActions providesMenuActions(GeoBeagle geoBeagle, Resources resources,
            MenuActionGoogleMaps menuActionGoogleMaps) {

        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(geoBeagle), new MenuActionEditGeocache(geoBeagle),
                // new MenuActionLogDnf(this), new MenuActionLogFind(this),
                new MenuActionSearchOnline(geoBeagle), new MenuActionSettings(geoBeagle),
                menuActionGoogleMaps
        };
        return new MenuActions(resources, menuActionArray);
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
    public GeocacheViewer providesGeocacheViewer(RadarView radarView, Activity activity,
            IconOverlayFactory iconOverlayFactory, MapViewBitmapCopier mapViewBitmapCopier,
            IconRenderer iconRenderer, DifficultyAndTerrainPainter difficultyAndTerrainPainter,
            NameFormatter nameFormatter, Resources resources, RatingsArray ratingsArray) {
        final TextView textViewName = (TextView)activity.findViewById(R.id.gcname);
        final ImageView cacheTypeImageView = (ImageView)activity.findViewById(R.id.gcicon);
        final NameViewer gcName = new NameViewer(textViewName, nameFormatter);

        final AttributeViewer gcDifficulty = getLabelledAttributeViewer(activity, resources,
                ratingsArray, new int[] {
                        R.drawable.ribbon_unselected_dark, R.drawable.ribbon_half_bright,
                        R.drawable.ribbon_selected_bright
                }, R.id.gc_difficulty, R.id.gc_text_difficulty);
        
        final AttributeViewer gcTerrain = getLabelledAttributeViewer(activity, resources,
                ratingsArray, new int[] {
                        R.drawable.paw_unselected_dark, R.drawable.paw_half_light,
                        R.drawable.paw_selected_light
                }, R.id.gc_terrain, R.id.gc_text_terrain);
        final ResourceImages gcContainer = new ResourceImages((ImageView)activity
                .findViewById(R.id.gccontainer), Arrays.asList(GeocacheViewer.CONTAINER_IMAGES));

        return new GeocacheViewer(radarView, activity, gcName, cacheTypeImageView, gcDifficulty,
                gcTerrain, gcContainer, iconOverlayFactory, mapViewBitmapCopier, iconRenderer,
                difficultyAndTerrainPainter);
    }

    private AttributeViewer getLabelledAttributeViewer(Activity activity, Resources resources,
            RatingsArray ratingsArray, int[] resourceIds, int difficultyId, int labelId) {
        final ImageView imageViewTerrain = (ImageView)activity.findViewById(difficultyId);

        final Drawable[] pawDrawables = {
                resources.getDrawable(resourceIds[0]), resources.getDrawable(resourceIds[1]),
                resources.getDrawable(resourceIds[2]),
        };
        final AttributeViewer pawImages = getImagesOnDifficulty(pawDrawables, imageViewTerrain,
                ratingsArray);
        final AttributeViewer gcTerrain = new LabelledAttributeViewer((TextView)activity
                .findViewById(labelId), pawImages);
        return gcTerrain;
    }
    
    @ChooseNavDialog
    @Provides
    public AlertDialog chooseNavDialogProvider(Provider<Resources> resourcesProvider,
            Provider<Context> contextProvider, GeoBeagle geoBeagle, ErrorDisplayer errorDisplayer,
            IntentFactory intentFactory) {
        final GeocacheToGoogleGeo geocacheToGoogleMaps = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.google_maps_intent);
        final GeocacheToGoogleGeo geocacheToNavigate = new GeocacheToGoogleGeo(resourcesProvider,
                R.string.navigate_intent);

        final IntentStarterGeo intentStarterRadar = new IntentStarterGeo(geoBeagle, new Intent(
                "com.google.android.radar.SHOW_RADAR"));
        final IntentStarterViewUri intentStarterGoogleMaps = new IntentStarterViewUri(geoBeagle,
                intentFactory, geocacheToGoogleMaps, errorDisplayer);
        final IntentStarterViewUri intentStarterNavigate = new IntentStarterViewUri(geoBeagle,
                intentFactory, geocacheToNavigate, errorDisplayer);
        final IntentStarter[] intentStarters = {
                intentStarterRadar, intentStarterGoogleMaps, intentStarterNavigate
        };
        OnClickListener onClickListener = new MenuActionGoogleMaps.OnClickListener(intentStarters);
        return new AlertDialog.Builder(contextProvider.get()).setItems(R.array.select_nav_choices,
                onClickListener).setTitle(R.string.select_nav_choices_title).create();
    }
}
