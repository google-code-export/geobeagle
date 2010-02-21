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

package com.google.code.geobeagle;

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.GeocacheFactory.Provider;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.activity.main.GeoUtils;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DbFrontend;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;

/**
 * Geocache or letterbox description, id, and coordinates.
 */
public class Geocache {
    static interface AttributeFormatter {
        CharSequence formatAttributes(int difficulty, int terrain);
    }

    static class AttributeFormatterImpl implements AttributeFormatter {
        public CharSequence formatAttributes(int difficulty, int terrain) {
            return (difficulty / 2.0) + " / " + (terrain / 2.0);
        }
    }

    static class AttributeFormatterNull implements AttributeFormatter {
        public CharSequence formatAttributes(int difficulty, int terrain) {
            return "";
        }
    }

    public static final String CACHE_TYPE = "cacheType";
    public static final String CONTAINER = "container";
    public static final String DIFFICULTY = "difficulty";
    public static final String ID = "id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String NAME = "name";

    public static final String SOURCE_NAME = "sourceName";
    public static final String SOURCE_TYPE = "sourceType";
    public static final String TERRAIN = "terrain";
    private final AttributeFormatter mAttributeFormatter;
    private final CacheType mCacheType;
    private final int mContainer;
    /** Difficulty rating * 2 (difficulty=1.5 => mDifficulty=3) */
    private final int mDifficulty;
    private GeoPoint mGeoPoint;
    private final CharSequence mId;
    private final double mLatitude;
    private final double mLongitude;
    private final CharSequence mName;
    private final String mSourceName;
    private final Source mSourceType;
    private final int mTerrain;
    
    private Drawable mIcon = null;
    //private Drawable mIconBig = null;
    private Drawable mIconMap = null;

    public Geocache(CharSequence id, CharSequence name, double latitude, double longitude,
            Source sourceType, String sourceName, CacheType cacheType, int difficulty, int terrain,
            int container, AttributeFormatter attributeFormatter) {
        mId = id;
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
        mSourceType = sourceType;
        mSourceName = sourceName;
        mCacheType = cacheType;
        mDifficulty = difficulty;
        mTerrain = terrain;
        mContainer = container;
        mAttributeFormatter = attributeFormatter;
    }

    /*
    public float[] calculateDistanceAndBearing(Location here) {
        if (here != null) {
            Location.distanceBetween(here.getLatitude(), here.getLongitude(), getLatitude(),
                    getLongitude(), mDistanceAndBearing);

            return mDistanceAndBearing;
        }
        mDistanceAndBearing[0] = -1;
        mDistanceAndBearing[1] = -1;
        return mDistanceAndBearing;
    }
    */

    public int describeContents() {
        return 0;
    }

    public CacheType getCacheType() {
        return mCacheType;
    }

    public int getContainer() {
        return mContainer;
    }
    
    public Drawable getIcon(Resources resources, GraphicsGenerator graphicsGenerator,
            DbFrontend dbFrontend) {
        if (mIcon == null) {
            Drawable overlayIcon = null;
            if (dbFrontend.geocacheHasTag(getId(), Tags.MINE))
                overlayIcon = resources.getDrawable(R.drawable.overlay_mine_cacheview);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.FOUND))
                overlayIcon = resources.getDrawable(R.drawable.overlay_found_cacheview);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.DNF))
                overlayIcon = resources.getDrawable(R.drawable.overlay_dnf_cacheview);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.NEW))
                overlayIcon = resources.getDrawable(R.drawable.overlay_new_cacheview);
            
            mIcon = graphicsGenerator.createIcon(this, overlayIcon, resources);
        }
        return mIcon;
    }

    public Drawable getIconMap(Resources resources, GraphicsGenerator graphicsGenerator,
            DbFrontend dbFrontend) {
        if (mIconMap == null) {
            Drawable overlayIcon = null;
            if (dbFrontend.geocacheHasTag(getId(), Tags.MINE))
                overlayIcon = resources.getDrawable(R.drawable.overlay_mine);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.FOUND))
                overlayIcon = resources.getDrawable(R.drawable.overlay_found);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.DNF))
                overlayIcon = resources.getDrawable(R.drawable.overlay_dnf);
            else if (dbFrontend.geocacheHasTag(getId(), Tags.NEW))
                overlayIcon = resources.getDrawable(R.drawable.overlay_new);
            
            mIconMap = graphicsGenerator.createIconMap(this, overlayIcon, resources);
        }
        return mIconMap;
    }
    
    public GeocacheFactory.Provider getContentProvider() {
        // Must use toString() rather than mId.subSequence(0,2).equals("GC"),
        // because editing the text in android produces a SpannableString rather
        // than a String, so the CharSequences won't be equal.
        String prefix = mId.subSequence(0, 2).toString();
        for (Provider provider : GeocacheFactory.ALL_PROVIDERS) {
            if (prefix.equals(provider.getPrefix()))
                return provider;
        }
        return Provider.GROUNDSPEAK;
    }

    public int getDifficulty() {
        return mDifficulty;
    }

    //Formerly calculateDistanceFast
    public float getDistanceTo(double latitude, double longitude) {
        double dLat = Math.toRadians(latitude - mLatitude);
        double dLon = Math.toRadians(longitude - mLongitude);
        final float sinDLat = FloatMath.sin((float)(dLat / 2));
        final float sinDLon = FloatMath.sin((float)(dLon / 2));
        float a = sinDLat * sinDLat + FloatMath.cos((float)Math.toRadians(mLatitude))
                * FloatMath.cos((float)Math.toRadians(latitude)) * sinDLon * sinDLon;
        float c = (float)(2 * Math.atan2(FloatMath.sqrt(a), FloatMath.sqrt(1 - a)));
        return 6371000 * c;
    }
    
    public CharSequence getFormattedAttributes() {
        return mAttributeFormatter.formatAttributes(mDifficulty, mTerrain);
    }

    public GeoPoint getGeoPoint() {
        if (mGeoPoint == null) {
            int latE6 = (int)(mLatitude * GeoUtils.MILLION);
            int lonE6 = (int)(mLongitude * GeoUtils.MILLION);
            mGeoPoint = new GeoPoint(latE6, lonE6);
        }
        return mGeoPoint;
    }

    public CharSequence getId() {
        return mId;
    }

    public CharSequence getIdAndName() {
        if (mId.length() == 0)
            return mName;
        else if (mName.length() == 0)
            return mId;
        else
            return mId + ": " + mName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public CharSequence getName() {
        return mName;
    }

    public CharSequence getShortId() {
        if (mId.length() > 2)
            return mId.subSequence(2, mId.length());
        return "";
    }

    public String getSourceName() {
        return mSourceName;
    }

    public Source getSourceType() {
        return mSourceType;
    }

    public int getTerrain() {
        return mTerrain;
    }

    /** @return true if the database needed to be updated */
    public boolean saveToDbIfNeeded(DbFrontend dbFrontend) {
        CacheWriter cacheWriter = dbFrontend.getCacheWriter();
        cacheWriter.startWriting();
        boolean changed =
        cacheWriter.conditionallyWriteCache(getId(), getName(), getLatitude(), 
                getLongitude(), getSourceType(), getSourceName(), 
                getCacheType(), getDifficulty(), getTerrain(), getContainer());
        cacheWriter.stopWriting();
        return changed;        
    }
    
    public void saveToDb(DbFrontend dbFrontend) {
        CacheWriter cacheWriter = dbFrontend.getCacheWriter();
        cacheWriter.startWriting();
        cacheWriter.insertAndUpdateCache(getId(), getName(), getLatitude(), 
                getLongitude(), getSourceType(), getSourceName(), 
                getCacheType(), getDifficulty(), getTerrain(), getContainer());
        cacheWriter.stopWriting();
    }

    /** The icons will be recalculated the next time they are needed. */
    public void flushIcons() {
        mIcon = null;
        mIconMap = null;
    }
}
