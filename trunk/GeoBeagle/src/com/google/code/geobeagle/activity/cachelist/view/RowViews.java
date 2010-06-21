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

package com.google.code.geobeagle.activity.cachelist.view;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlay;
import com.google.code.geobeagle.GraphicsGenerator.IconOverlayFactory;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.ListViewBitmapCopier;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.presenter.BearingFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

class RowViews {
    private final TextView mAttributes;
    private final TextView mCacheName;
    private final TextView mDistance;
    private final ImageView mIcon;
    private final GraphicsGenerator.IconOverlayFactory mIconOverlayFactory;
    private final TextView mId;
    private final NameFormatter mNameFormatter;

    RowViews(TextView attributes, TextView cacheName, TextView distance, ImageView icon,
            TextView id, IconOverlayFactory iconOverlayFactory, NameFormatter nameFormatter) {
        mAttributes = attributes;
        mCacheName = cacheName;
        mDistance = distance;
        mIcon = icon;
        mId = id;
        mIconOverlayFactory = iconOverlayFactory;
        mNameFormatter = nameFormatter;
    }

    void set(GeocacheVector geocacheVector, BearingFormatter relativeBearingFormatter,
            DistanceFormatter distanceFormatter, ListViewBitmapCopier listViewBitmapCopier,
            IconRenderer iconRenderer) {
        Geocache geocache = geocacheVector.getGeocache();
        IconOverlay iconOverlay = mIconOverlayFactory.create(geocache, false);
        mNameFormatter.format(mCacheName, geocache.getAvailable(), geocache.getArchived());

        final Drawable icon = iconRenderer.renderIcon(geocache.getDifficulty(), geocache
                .getTerrain(), geocache.getCacheType().icon(), iconOverlay, listViewBitmapCopier);

        mIcon.setImageDrawable(icon);
        mId.setText(geocacheVector.getId());
        mAttributes.setText(geocacheVector.getFormattedAttributes());
        mCacheName.setText(geocacheVector.getName());

        mDistance.setText(geocacheVector.getFormattedDistance(distanceFormatter,
                relativeBearingFormatter));
    }
}
