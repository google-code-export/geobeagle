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

import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.compass.ChooseNavDialog;
import com.google.code.geobeagle.activity.compass.CompassActivityOnCreateHandler;
import com.google.code.geobeagle.activity.compass.CompassFragmentOnCreateHandler;
import com.google.code.geobeagle.activity.compass.CompassFragtivityOnCreateHandler;
import com.google.code.geobeagle.activity.compass.RadarView;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.ResourceImages;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.inject.Injector;
import com.google.inject.Provides;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class CompassActivityModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
        bind(ChooseNavDialog.class).toProvider(ChooseNavDialogProvider.class);
        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion >= Build.VERSION_CODES.HONEYCOMB) {
            bind(CompassFragtivityOnCreateHandler.class).to(CompassFragmentOnCreateHandler.class);
        } else {
            bind(CompassFragtivityOnCreateHandler.class).to(CompassActivityOnCreateHandler.class);
        }
    }

    private UnlabelledAttributeViewer getImagesOnDifficulty(final Drawable[] pawDrawables,
            ImageView imageView, RatingsArray ratingsArray) {
        return new UnlabelledAttributeViewer(imageView, ratingsArray.getRatings(pawDrawables, 10));
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
    XmlPullParser providesXmlPullParser() throws XmlPullParserException {
        return XmlPullParserFactory.newInstance().newPullParser();
    }

    @Provides
    public GeocacheViewer providesGeocacheViewer(Injector injector) {
        RadarView radarView = injector.getInstance(RadarView.class);
        Activity activity = injector.getInstance(Activity.class);
        IconOverlayFactory iconOverlayFactory = injector.getInstance(IconOverlayFactory.class);
        NameFormatter nameFormatter = injector.getInstance(NameFormatter.class);
        Resources resources = injector.getInstance(Resources.class);
        RatingsArray ratingsArray = injector.getInstance(RatingsArray.class);
        MapViewBitmapCopier mapViewBitmapCopier = injector.getInstance(MapViewBitmapCopier.class);
        DifficultyAndTerrainPainter difficultyAndTerrainPainter = injector
                .getInstance(DifficultyAndTerrainPainter.class);
        IconRenderer iconRenderer = injector.getInstance(IconRenderer.class);

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
        final ResourceImages gcContainer = new ResourceImages(
                (TextView)activity.findViewById(R.id.gc_text_container),
                (ImageView)activity.findViewById(R.id.gccontainer),
                Arrays.asList(GeocacheViewer.CONTAINER_IMAGES));

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
}
