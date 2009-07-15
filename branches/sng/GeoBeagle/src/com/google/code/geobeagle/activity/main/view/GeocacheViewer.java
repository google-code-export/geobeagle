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

        public LabelledAttributeViewer(int[] images, TextView label, ImageView imageView) {
            mUnlabelledAttributeViewer = new UnlabelledAttributeViewer(images, imageView);
            mLabel = label;
        }

        @Override
        public void setImage(int attributeValue) {
            mUnlabelledAttributeViewer.setImage(attributeValue);
            mLabel.setVisibility(attributeValue == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public static class UnlabelledAttributeViewer implements AttributeViewer {
        private final int[] mImages;
        private final ImageView mImageView;

        public UnlabelledAttributeViewer(int[] images, ImageView imageView) {
            mImages = images;
            mImageView = imageView;
        }

        public void setImage(int attributeValue) {
            if (attributeValue == 0) {
                mImageView.setVisibility(View.GONE);
                return;
            }
            mImageView.setImageResource(mImages[attributeValue - 1]);
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

    public static final int CACHE_TYPE_IMAGES[] = {
            R.drawable.cache_tradi_big, R.drawable.cache_multi_big, R.drawable.cache_mystery_big,
            R.drawable.blue_dot
    };
    public static final int CONTAINER_IMAGES[] = {
            R.drawable.size_1, R.drawable.size_2, R.drawable.size_3, R.drawable.size_4
    };
    public static final int STAR_IMAGES[] = {
            R.drawable.stars_1, R.drawable.stars_2, R.drawable.stars_3, R.drawable.stars_4,
            R.drawable.stars_5, R.drawable.stars_6, R.drawable.stars_7, R.drawable.stars_8,
            R.drawable.stars_9, R.drawable.stars_10
    };
    private final AttributeViewer mCacheType;
    private final AttributeViewer mContainer;
    private final AttributeViewer mDifficulty;
    private final TextView mId;
    private final NameViewer mName;
    private final RadarView mRadarView;
    private final AttributeViewer mTerrain;

    public GeocacheViewer(RadarView radarView, TextView gcId, NameViewer gcName,
            AttributeViewer gcIcon, AttributeViewer gcDifficulty, AttributeViewer gcTerrain,
            UnlabelledAttributeViewer gcContainer) {
        mRadarView = radarView;
        mId = gcId;
        mName = gcName;
        mCacheType = gcIcon;
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

        mCacheType.setImage(geocache.getCacheType().toInt());
        mContainer.setImage(geocache.getContainer());
        mDifficulty.setImage(geocache.getDifficulty());
        mTerrain.setImage(geocache.getTerrain());

        mName.set(geocache.getName());
    }
}
