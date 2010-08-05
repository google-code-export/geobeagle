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

package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlay;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.view.NameFormatter;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.main.RadarView;
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
        private final AttributeViewer mUnlabelledAttributeViewer;
        private final TextView mLabel;

        public LabelledAttributeViewer(TextView label, AttributeViewer unlabelledAttributeViewer) {
            mUnlabelledAttributeViewer = unlabelledAttributeViewer;
            mLabel = label;
        }

        @Override
        public void setImage(int attributeValue) {
            mUnlabelledAttributeViewer.setImage(attributeValue);
            mLabel.setVisibility(attributeValue == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public static class UnlabelledAttributeViewer implements AttributeViewer {
        private final Drawable[] mDrawables;
        private final ImageView mImageView;

        public UnlabelledAttributeViewer(ImageView imageView, Drawable[] drawables) {
            mImageView = imageView;
            mDrawables = drawables;
        }

        @Override
        public void setImage(int attributeValue) {
            if (attributeValue == 0) {
                mImageView.setVisibility(View.GONE);
                return;
            }
            mImageView.setImageDrawable(mDrawables[attributeValue-1]);
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    public static class ResourceImages implements AttributeViewer {
        private final List<Integer> mResources;
        private final ImageView mImageView;
        private final TextView mLabel;

        public ResourceImages(TextView label, ImageView imageView, List<Integer> resources) {
            mLabel = label;
            mImageView = imageView;
            mResources = resources;
        }

        @Override
        public void setImage(int attributeValue) {
            mImageView.setImageResource(mResources.get(attributeValue));
        }
        
        public void setVisibility(int visibility) {
            mImageView.setVisibility(visibility);
            mLabel.setVisibility(visibility);
        }
        
    }
 
    public static class NameViewer {
        private final TextView mName;
        private final NameFormatter mNameFormatter;

        @Inject
        public NameViewer(@Named("GeocacheName") TextView name, NameFormatter nameFormatter) {
            mName = name;
            mNameFormatter = nameFormatter;
        }

        void set(CharSequence name, boolean available, boolean archived) {
            if (name.length() == 0) {
                mName.setVisibility(View.GONE);
                return;
            }
            mName.setText(name);
            mName.setVisibility(View.VISIBLE);
            mNameFormatter.format(mName, available, archived);
        }
    }

    public static final Integer CONTAINER_IMAGES[] = {
            R.drawable.size_0, R.drawable.size_1, R.drawable.size_2, R.drawable.size_3,
            R.drawable.size_4, R.drawable.size_5
    };

    private final ImageView mCacheTypeImageView;
    private final ResourceImages mContainer;
    private final AttributeViewer mDifficulty;
    private final NameViewer mName;
    private final RadarView mRadarView;
    private final AttributeViewer mTerrain;
    private final IconOverlayFactory mIconOverlayFactory;
    private final MapViewBitmapCopier mMapViewBitmapCopier;
    private final IconRenderer mIconRenderer;
    private final Activity mActivity;
    private final DifficultyAndTerrainPainter mDifficultyAndTerrainPainter;

    public GeocacheViewer(RadarView radarView, Activity activity, NameViewer gcName,
            ImageView cacheTypeImageView,
            AttributeViewer gcDifficulty,
            AttributeViewer gcTerrain, ResourceImages gcContainer,
            IconOverlayFactory iconOverlayFactory, MapViewBitmapCopier mapViewBitmapCopier,
            IconRenderer iconRenderer, DifficultyAndTerrainPainter difficultyAndTerrainPainter) {
        mRadarView = radarView;
        mActivity = activity;
        mName = gcName;
        mCacheTypeImageView = cacheTypeImageView;
        mDifficulty = gcDifficulty;
        mTerrain = gcTerrain;
        mContainer = gcContainer;
        mIconOverlayFactory = iconOverlayFactory;
        mMapViewBitmapCopier = mapViewBitmapCopier;
        mIconRenderer = iconRenderer;
        mDifficultyAndTerrainPainter = difficultyAndTerrainPainter;
    }

    public void set(Geocache geocache) {
        final double latitude = geocache.getLatitude();
        final double longitude = geocache.getLongitude();
        mRadarView.setTarget((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));
        mActivity.setTitle("GeoBeagle: " + geocache.getId());

        IconOverlay iconOverlay = mIconOverlayFactory.create(geocache, true);
        int iconBig = geocache.getCacheType().iconBig();
        Drawable icon = mIconRenderer.renderIcon(0, 0, iconBig, iconOverlay, mMapViewBitmapCopier,
                mDifficultyAndTerrainPainter);
        mCacheTypeImageView.setImageDrawable(icon);
        mContainer.setVisibility(geocache.getContainer() == 0 ? View.GONE : View.VISIBLE);
        mContainer.setImage(geocache.getContainer());
        mDifficulty.setImage(geocache.getDifficulty());
        mTerrain.setImage(geocache.getTerrain());

        mName.set(geocache.getName(), geocache.getAvailable(), geocache.getArchived());
    }
}
