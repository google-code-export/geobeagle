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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.cachelist.presenter.GeocacheSummaryRowInflater.RowViews.CacheNameAttributes;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.database.DistanceAndBearing;
import com.google.code.geobeagle.formatting.DistanceFormatter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GeocacheSummaryRowInflater implements HasDistanceFormatter {
    public static class RowViews {
        private final TextView mAttributes;
        private final TextView mCacheName;
        private final TextView mDistance;
        private final ImageView mIcon;
        private final TextView mId;
        private final Resources mResources;
        private final CacheNameAttributes mCacheNameAttributes;

        RowViews(TextView attributes, TextView cacheName, TextView distance, ImageView icon,
                TextView id, Resources resources, CacheNameAttributes cacheNameAttributes) {
            mAttributes = attributes;
            mCacheName = cacheName;
            mDistance = distance;
            mIcon = icon;
            mId = id;
            mResources = resources;
            mCacheNameAttributes = cacheNameAttributes;
        }

        public static class CacheNameAttributes {

            void setTextColor(CharSequence geocacheId, boolean fArchived,
                    boolean fUnavailable, TextView cacheName) {
                if (fArchived)
                    cacheName.setTextColor(Color.DKGRAY);
                else if (fUnavailable)
                    cacheName.setTextColor(Color.LTGRAY);
                else
                    cacheName.setTextColor(Color.WHITE);
            }

            void setStrikethrough(CharSequence geocacheId, boolean fArchived,
                    boolean fUnavailable, TextView cacheName) {
                if (fArchived || fUnavailable)
                    cacheName.setPaintFlags(cacheName.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    cacheName.setPaintFlags(cacheName.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        
        private CharSequence getFormattedDistance(
                DistanceAndBearing distanceAndBearing, float azimuth,
                DistanceFormatter distanceFormatter,
                BearingFormatter relativeBearingFormatter) {
            // Use the slower, more accurate distance for display.
            double distance = distanceAndBearing.getDistance();
            if (distance == -1) {
                return "";
            }

            final CharSequence formattedDistance = distanceFormatter
                    .formatDistance((float)distance);
            float bearing = (float)distanceAndBearing.getBearing();
            final String formattedBearing = relativeBearingFormatter.formatBearing(bearing,
                    azimuth);
            return formattedDistance + " " + formattedBearing;
        }

        void set(DistanceAndBearing distanceAndBearing, float azimuth,
                DistanceFormatter distanceFormatter,
                BearingFormatter relativeBearingFormatter,
                GraphicsGenerator graphicsGenerator, DbFrontend dbFrontend) {
            //CacheType type = geocacheVector.getGeocache().getCacheType();
            //mIcon.setImageResource(type.icon());
            Geocache geocache = distanceAndBearing.getGeocache();
            mIcon.setImageDrawable(geocache.getIcon(mResources, graphicsGenerator, dbFrontend));
            CharSequence geocacheId = geocache.getId();
            mId.setText(geocacheId);
            mAttributes.setText(geocache.getFormattedAttributes());
            mCacheName.setText(geocache.getName());
            boolean fArchived = dbFrontend.geocacheHasTag(geocacheId,
                    Tags.ARCHIVED);
            boolean fUnavailable = dbFrontend.geocacheHasTag(geocacheId,
                    Tags.UNAVAILABLE);
            mCacheNameAttributes.setTextColor(geocacheId, fArchived,
                    fUnavailable, mCacheName);
            mCacheNameAttributes.setStrikethrough(geocacheId, fArchived,
                    fUnavailable, mCacheName);
            mDistance.setText(getFormattedDistance(distanceAndBearing, azimuth, distanceFormatter,
                    relativeBearingFormatter));
        }
    }

    private BearingFormatter mBearingFormatter;
    private DistanceFormatter mDistanceFormatter;
    private final LayoutInflater mLayoutInflater;
    private final Resources mResources;
    private final GraphicsGenerator mGraphicsGenerator;
    private final DbFrontend mDbFrontend;
    private final CacheNameAttributes mCacheNameAttributes;

    public GeocacheSummaryRowInflater(DistanceFormatter distanceFormatter,
            LayoutInflater layoutInflater,
            BearingFormatter relativeBearingFormatter, Resources resources,
            GraphicsGenerator graphicsGenerator, DbFrontend dbFrontend,
            CacheNameAttributes cacheNameAttributes) {
        mLayoutInflater = layoutInflater;
        mDistanceFormatter = distanceFormatter;
        mBearingFormatter = relativeBearingFormatter;
        mResources = resources;
        mGraphicsGenerator = graphicsGenerator;
        mDbFrontend = dbFrontend;        
        mCacheNameAttributes = cacheNameAttributes;
    }

    BearingFormatter getBearingFormatter() {
        return mBearingFormatter;
    }

    public View inflate(View convertView) {
        if (convertView != null)
            return convertView;
        // Log.d("GeoBeagle", "SummaryRow::inflate(" + convertView + ")");

        View view = mLayoutInflater.inflate(R.layout.cache_row, null);
        RowViews rowViews = new RowViews((TextView)view
                .findViewById(R.id.txt_gcattributes), ((TextView)view
                .findViewById(R.id.txt_cache)), ((TextView)view
                .findViewById(R.id.distance)), ((ImageView)view
                .findViewById(R.id.gc_row_icon)), ((TextView)view
                .findViewById(R.id.txt_gcid)), mResources, mCacheNameAttributes);
        view.setTag(rowViews);
        return view;
    }

    public void setBearingFormatter(boolean absoluteBearing) {
        mBearingFormatter = absoluteBearing ? new AbsoluteBearingFormatter()
                : new RelativeBearingFormatter();
    }

    public void setData(View view, DistanceAndBearing geocacheVector, float azimuth) {
        ((RowViews)view.getTag()).set(geocacheVector, azimuth, mDistanceFormatter,
                mBearingFormatter, mGraphicsGenerator, mDbFrontend);
    }

    public void setDistanceFormatter(DistanceFormatter distanceFormatter) {
        mDistanceFormatter = distanceFormatter;
    }
}
