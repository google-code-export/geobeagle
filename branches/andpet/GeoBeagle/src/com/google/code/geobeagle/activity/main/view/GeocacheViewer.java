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
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.activity.main.RadarView;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GeocacheViewer {
    public interface AttributeViewer {
        void setImage(int attributeValue);
    }

    public static class LabelledAttributeViewer implements AttributeViewer {
        private final UnlabelledAttributeViewer mUnlabelledAttributeViewer;
        private final TextView mLabel;

        public LabelledAttributeViewer(TextView label, ImageView imageView,
                AttributeViewer imageCollection) {
            mUnlabelledAttributeViewer = new UnlabelledAttributeViewer(
                    imageView, imageCollection);
            mLabel = label;
        }

        @Override
        public void setImage(int attributeValue) {
            mUnlabelledAttributeViewer.setImage(attributeValue);
            mLabel.setVisibility(attributeValue == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public static class DrawableImages implements AttributeViewer {
        private final Drawable[] mDrawables;
        private final ImageView mImageView;

        public DrawableImages(ImageView imageView, Drawable[] drawables) {
            mImageView = imageView;
            mDrawables = drawables;
        }

        @Override
        public void setImage(int attributeValue) {
            mImageView.setImageDrawable(mDrawables[attributeValue]);
        }
    }

    public static class ResourceImages implements AttributeViewer {
        private final int[] mResources;
        private final ImageView mImageView;

        public ResourceImages(ImageView imageView, int[] resources) {
            mImageView = imageView;
            mResources = resources;
        }

        @Override
        public void setImage(int attributeValue) {
            mImageView.setImageResource(mResources[attributeValue]);
        }
    }
    
    public static class UnlabelledAttributeViewer implements AttributeViewer {
        private final ImageView mImageView;
        private final AttributeViewer mImageCollection;

        public UnlabelledAttributeViewer(ImageView imageView,
                AttributeViewer imageCollection) {
            mImageView = imageView;
            mImageCollection = imageCollection;
        }

        public void setImage(int attributeValue) {
            if (attributeValue == 0) {
                mImageView.setVisibility(View.GONE);
                return;
            }
            mImageCollection.setImage(attributeValue - 1);
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    public static class NameViewer {
        private final TextView mName;

        public NameViewer(TextView name) {
            mName = name;
        }

        void set(CharSequence name) {
            if (name.length() == 0) {
                mName.setVisibility(View.GONE);
                return;
            }
            mName.setText(name);
            mName.setVisibility(View.VISIBLE);
        }
    }

    public static final int CONTAINER_IMAGES[] = {
            R.drawable.size_1, R.drawable.size_2, R.drawable.size_3, R.drawable.size_4
    };
    
    private final ImageView mCacheTypeImageView;
    private final AttributeViewer mContainer;
    private final AttributeViewer mDifficulty;
    private final TextView mId;
    private final NameViewer mName;
    private final RadarView mRadarView;
    private final AttributeViewer mTerrain;

    public GeocacheViewer(RadarView radarView, TextView gcId, NameViewer gcName,
            ImageView cacheTypeImageView, AttributeViewer gcDifficulty, AttributeViewer gcTerrain,
            UnlabelledAttributeViewer gcContainer) {
        mRadarView = radarView;
        mId = gcId;
        mName = gcName;
        mCacheTypeImageView = cacheTypeImageView;
        mDifficulty = gcDifficulty;
        mTerrain = gcTerrain;
        mContainer = gcContainer;
    }

    
    public void set(Geocache geocache) {
        final double latitude = geocache.getLatitude();
        final double longitude = geocache.getLongitude();
        mRadarView.setTarget((int)(latitude * GeoUtils.MILLION),
                (int)(longitude * GeoUtils.MILLION));
        mId.setText(geocache.getId());

        mCacheTypeImageView.setImageResource(geocache.getCacheType().iconBig());
        mContainer.setImage(geocache.getContainer());
        mDifficulty.setImage(geocache.getDifficulty());
        mTerrain.setImage(geocache.getTerrain());

        mName.set(geocache.getName());
    }
}
