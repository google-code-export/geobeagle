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

import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Geocache or letterbox description, id, and coordinates.
 */
public class Geocache implements Parcelable {
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
    public static Parcelable.Creator<Geocache> CREATOR = new GeocacheFactory.CreateGeocacheFromParcel();
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
    private float[] mDistanceAndBearing = new float[2];
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

    Geocache(CharSequence id, CharSequence name, double latitude, double longitude,
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

    public int describeContents() {
        return 0;
    }

    public CacheType getCacheType() {
        return mCacheType;
    }

    public int getContainer() {
        return mContainer;
    }
    
    private static Paint mTempPaint = new Paint();
    private static Rect mTempRect = new Rect();
    private Drawable createIcon(Bitmap bitmap, int thickness, int bottom) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);

        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();

        mTempPaint.setColor(Color.RED);
        int diffHeight = (int)((imageHeight - bottom - 1) * (mDifficulty/10.0));
        mTempRect.set(1, imageHeight-1-diffHeight-bottom, thickness+1, imageHeight-1-bottom);
        canvas.drawRect(mTempRect, mTempPaint);

        mTempPaint.setARGB(255, 0xDB, 0xA1, 0x09);
        int terrHeight = (int)((imageHeight - bottom - 1) * (mTerrain/10.0));
        mTempRect.set(imageWidth-thickness-1, imageHeight-1-terrHeight-bottom, 
                imageWidth-1, imageHeight-1-bottom);
        canvas.drawRect(mTempRect, mTempPaint);

        return new BitmapDrawable(copy);
    }

    public Drawable getIcon(Resources resources) {
        if (mIcon == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(resources, getCacheType().icon());
            mIcon = createIcon(bitmap, 3, 1);
        }
        return mIcon;
    }

    /*
    public Drawable getIconBig(Resources resources) {
        if (mIconBig == null) {
            if (mResources == null)
                return null;
            mIconBig = createIcon(getCacheType().iconBig(), 6, 1);
            Log.d("GeoBeagle", "Created iconBig " + mIconBig.getIntrinsicWidth() + 
                    " x " + mIconBig.getIntrinsicHeight());
        }
        return mIconBig;
    }
    */

    public Drawable getIconMap(Resources resources) {
        if (mIconMap == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(resources, getCacheType().iconMap());
            mIconMap = createIcon(bitmap, 2, 5);
            int width = mIconMap.getIntrinsicWidth();
            int height = mIconMap.getIntrinsicHeight();
            mIconMap.setBounds(-width/2, -height, width/2, 0);
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

    public void saveLocation(DbFrontend dbFrontend) {
        CacheWriter cacheWriter = dbFrontend.getCacheWriter();
        cacheWriter.startWriting();
        cacheWriter.insertAndUpdateCache(getId(), getName(), getLatitude(), 
                getLongitude(), getSourceType(), getSourceName(), 
                getCacheType(), getDifficulty(), getTerrain(), getContainer());
        cacheWriter.stopWriting();
    }

    public void saveToBundle(Bundle bundle) {
        bundle.putCharSequence(ID, mId);
        bundle.putCharSequence(NAME, mName);
        bundle.putDouble(LATITUDE, mLatitude);
        bundle.putDouble(LONGITUDE, mLongitude);
        bundle.putInt(SOURCE_TYPE, mSourceType.toInt());
        bundle.putString(SOURCE_NAME, mSourceName);
        bundle.putInt(CACHE_TYPE, mCacheType.toInt());
        bundle.putInt(DIFFICULTY, mDifficulty);
        bundle.putInt(TERRAIN, mTerrain);
        bundle.putInt(CONTAINER, mContainer);
    }

    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        saveToBundle(bundle);
        out.writeBundle(bundle);
    }

    public void writeToPrefs(Editor editor) {
        // Must use toString(), see comment above in getCommentProvider.
        editor.putString(ID, mId.toString());
        editor.putString(NAME, mName.toString());
        editor.putFloat(LATITUDE, (float)mLatitude);
        editor.putFloat(LONGITUDE, (float)mLongitude);
        editor.putInt(SOURCE_TYPE, mSourceType.toInt());
        editor.putString(SOURCE_NAME, mSourceName);
        editor.putInt(CACHE_TYPE, mCacheType.toInt());
        editor.putInt(DIFFICULTY, mDifficulty);
        editor.putInt(TERRAIN, mTerrain);
        editor.putInt(CONTAINER, mContainer);
    }
}
