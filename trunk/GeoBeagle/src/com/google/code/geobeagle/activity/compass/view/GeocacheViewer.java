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

package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlay;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.compass.GeoUtils;
import com.google.code.geobeagle.activity.compass.RadarView;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GeocacheViewer {
    public interface AttributeViewer {
        void setImage(int attributeValue);
    }

    public static class LabelledAttributeViewer implements AttributeViewer {
        private final AttributeViewer unlabelledAttributeViewer;
        private final TextView label;

        public LabelledAttributeViewer(TextView label, AttributeViewer unlabelledAttributeViewer) {
            this.unlabelledAttributeViewer = unlabelledAttributeViewer;
            this.label = label;
        }

        @Override
        public void setImage(int attributeValue) {
            unlabelledAttributeViewer.setImage(attributeValue);
            label.setVisibility(attributeValue == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public static class UnlabelledAttributeViewer implements AttributeViewer {
        private final Drawable[] drawables;
        private final ImageView imageView;

        public UnlabelledAttributeViewer(ImageView imageView, Drawable[] drawables) {
            this.imageView = imageView;
            this.drawables = drawables;
        }

        @Override
        public void setImage(int attributeValue) {
            if (attributeValue == 0) {
                imageView.setVisibility(View.GONE);
                return;
            }
            imageView.setImageDrawable(drawables[attributeValue-1]);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public static class ResourceImages implements AttributeViewer {
        private final List<Integer> resources;
        private final ImageView imageView;
        private final TextView label;

        public ResourceImages(TextView label, ImageView imageView, List<Integer> resources) {
            this.label = label;
            this.imageView = imageView;
            this.resources = resources;
        }

        @Override
        public void setImage(int attributeValue) {
            imageView.setImageResource(resources.get(attributeValue));
        }
        
        public void setVisibility(int visibility) {
            imageView.setVisibility(visibility);
            label.setVisibility(visibility);
        }
        
    }
 
    public static class NameViewer {
        private final TextView name;
        private final NameFormatter nameFormatter;

        @Inject
        public NameViewer(@Named("GeocacheName") TextView name, NameFormatter nameFormatter) {
            this.name = name;
            this.nameFormatter = nameFormatter;
        }

        void set(CharSequence name, boolean available, boolean archived) {
            if (name.length() == 0) {
                this.name.setVisibility(View.GONE);
                return;
            }
            this.name.setText(name);
            this.name.setVisibility(View.VISIBLE);
            nameFormatter.format(this.name, available, archived);
        }
    }

    public static final Integer CONTAINER_IMAGES[] = {
            R.drawable.size_0, R.drawable.size_1, R.drawable.size_2, R.drawable.size_3,
            R.drawable.size_4, R.drawable.size_5
    };

    private final ImageView cacheTypeImageView;
    private final ResourceImages container;
    private final AttributeViewer difficulty;
    private final NameViewer name;
    private final RadarView radarView;
    private final AttributeViewer terrain;
    private final IconOverlayFactory iconOverlayFactory;
    private final MapViewBitmapCopier mapViewBitmapCopier;
    private final IconRenderer iconRenderer;
    private final Activity activity;
    private final DifficultyAndTerrainPainter difficultyAndTerrainPainter;

    public GeocacheViewer(RadarView radarView, Activity activity, NameViewer gcName,
            ImageView cacheTypeImageView,
            AttributeViewer gcDifficulty,
            AttributeViewer gcTerrain, ResourceImages gcContainer,
            IconOverlayFactory iconOverlayFactory, MapViewBitmapCopier mapViewBitmapCopier,
            IconRenderer iconRenderer, DifficultyAndTerrainPainter difficultyAndTerrainPainter) {
        this.radarView = radarView;
        this.activity = activity;
        this.name = gcName;
        this.cacheTypeImageView = cacheTypeImageView;
        this.difficulty = gcDifficulty;
        this.terrain = gcTerrain;
        this.container = gcContainer;
        this.iconOverlayFactory = iconOverlayFactory;
        this.mapViewBitmapCopier = mapViewBitmapCopier;
        this.iconRenderer = iconRenderer;
        this.difficultyAndTerrainPainter = difficultyAndTerrainPainter;
    }

    public void set(Geocache geocache) {
        double latitude = geocache.getLatitude();
        double longitude = geocache.getLongitude();
        radarView.setTarget((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));
        activity.setTitle("GeoBeagle: " + geocache.getId());

        IconOverlay iconOverlay = iconOverlayFactory.create(geocache, true);
        int iconBig = geocache.getCacheType().iconBig();
        Drawable icon = iconRenderer.renderIcon(0, 0, iconBig, iconOverlay, mapViewBitmapCopier,
                difficultyAndTerrainPainter);
        cacheTypeImageView.setImageDrawable(icon);
        int container = geocache.getContainer();
        this.container.setVisibility(container == 0 ? View.GONE : View.VISIBLE);
        this.container.setImage(container);
        difficulty.setImage(geocache.getDifficulty());
        terrain.setImage(geocache.getTerrain());

        name.set(geocache.getName(), geocache.getAvailable(), geocache.getArchived());
    }
}
