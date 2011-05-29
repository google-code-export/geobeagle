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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.compass.view.GeocacheViewer.ResourceImages;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class GeocacheViewerFactory {

    private final IconOverlayFactory iconOverlayFactory;
    private final NameFormatter nameFormatter;
    private final Resources resources;
    private final RatingsArray ratingsArray;
    private final MapViewBitmapCopier mapViewBitmapCopier;
    private final DifficultyAndTerrainPainter difficultyAndTerrainPainter;
    private final IconRenderer iconRenderer;
    private final Activity activity;

    @Inject
    GeocacheViewerFactory(IconOverlayFactory iconOverlayFactory,
            NameFormatter nameFormatter,
            Resources resources,
            RatingsArray ratingsArray,
            MapViewBitmapCopier mapViewBitmapCopier,
            DifficultyAndTerrainPainter difficultyAndTerrainPainter,
            IconRenderer iconRenderer,
            Activity activity) {
        this.iconOverlayFactory = iconOverlayFactory;
        this.nameFormatter = nameFormatter;
        this.resources = resources;
        this.ratingsArray = ratingsArray;
        this.mapViewBitmapCopier = mapViewBitmapCopier;
        this.difficultyAndTerrainPainter = difficultyAndTerrainPainter;
        this.iconRenderer = iconRenderer;
        this.activity = activity;
    }

    GeocacheViewer create(HasViewById hasViewById) {
        RadarView radarView = (RadarView)hasViewById.findViewById(R.id.radarview);
        radarView.setUseImperial(false);
        radarView.setDistanceView((TextView)hasViewById.findViewById(R.id.radar_distance),
                (TextView)hasViewById.findViewById(R.id.radar_bearing),
                (TextView)hasViewById.findViewById(R.id.radar_accuracy));

        TextView textViewName = (TextView)hasViewById.findViewById(R.id.gcname);
        ImageView cacheTypeImageView = (ImageView)hasViewById.findViewById(R.id.gcicon);
        NameViewer gcName = new NameViewer(textViewName, nameFormatter);

        AttributeViewer gcDifficulty = CompassActivityModule.getLabelledAttributeViewer(
                hasViewById, resources, ratingsArray, new int[] {
                        R.drawable.ribbon_unselected_dark, R.drawable.ribbon_half_bright,
                        R.drawable.ribbon_selected_bright
                }, R.id.gc_difficulty, R.id.gc_text_difficulty);

        AttributeViewer gcTerrain = CompassActivityModule.getLabelledAttributeViewer(hasViewById,
                resources, ratingsArray, new int[] {
                        R.drawable.paw_unselected_dark, R.drawable.paw_half_light,
                        R.drawable.paw_selected_light
                }, R.id.gc_terrain, R.id.gc_text_terrain);
        ResourceImages gcContainer = new ResourceImages(
                (TextView)hasViewById.findViewById(R.id.gc_text_container),
                (ImageView)hasViewById.findViewById(R.id.gccontainer),
                Arrays.asList(GeocacheViewer.CONTAINER_IMAGES));

        return new GeocacheViewer(radarView, activity, gcName, cacheTypeImageView, gcDifficulty,
                gcTerrain, gcContainer, iconOverlayFactory, mapViewBitmapCopier, iconRenderer,
                difficultyAndTerrainPainter);
    }
}
