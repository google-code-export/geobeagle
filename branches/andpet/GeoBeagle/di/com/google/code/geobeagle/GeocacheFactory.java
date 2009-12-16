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

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.Geocache.AttributeFormatter;
import com.google.code.geobeagle.Geocache.AttributeFormatterImpl;
import com.google.code.geobeagle.Geocache.AttributeFormatterNull;
import com.google.code.geobeagle.GeocacheFactory.Source.SourceFactory;
import com.google.code.geobeagle.database.SourceNameTranslator;

import android.database.Cursor;

import java.util.WeakHashMap;

//TODO: Make GeocacheFactory call DbFrontend and not the other way around.
//TODO: Make GeocacheFactory is a global object, common to all Activities of GeoBeagle. Solves GeocacheFactory flushing in other activites when a geocache is changed.
public class GeocacheFactory {
    public static enum Provider {
        ATLAS_QUEST(0, "LB"), GROUNDSPEAK(1, "GC"), MY_LOCATION(-1, "ML"), OPENCACHING(2, "OC");

        private final int mIx;
        private final String mPrefix;

        Provider(int ix, String prefix) {
            mIx = ix;
            mPrefix = prefix;
        }

        public int toInt() {
            return mIx;
        }

        public String getPrefix() {
            return mPrefix;
        }
    }

    public static Provider ALL_PROVIDERS[] = {
            Provider.ATLAS_QUEST, Provider.GROUNDSPEAK, Provider.MY_LOCATION, Provider.OPENCACHING
    };

    public static enum Source {
        GPX(0), LOC(3), MY_LOCATION(1), WEB_URL(2);

        public static class SourceFactory {
            private final Source mSources[] = new Source[values().length];

            public SourceFactory() {
                for (Source source : values())
                    mSources[source.mIx] = source;
            }

            public Source fromInt(int i) {
                return mSources[i];
            }
        }

        private final int mIx;

        Source(int ix) {
            mIx = ix;
        }

        public int toInt() {
            return mIx;
        }
    }

    static class AttributeFormatterFactory {
        private AttributeFormatterImpl mAttributeFormatterImpl;
        private AttributeFormatterNull mAttributeFormatterNull;

        public AttributeFormatterFactory(AttributeFormatterImpl attributeFormatterImpl,
                AttributeFormatterNull attributeFormatterNull) {
            mAttributeFormatterImpl = attributeFormatterImpl;
            mAttributeFormatterNull = attributeFormatterNull;
        }

        AttributeFormatter getAttributeFormatter(Source sourceType) {
            if (sourceType == Source.GPX)
                return mAttributeFormatterImpl;
            return mAttributeFormatterNull;
        }
    }

    private static CacheTypeFactory mCacheTypeFactory;
    private static SourceFactory mSourceFactory;
    private AttributeFormatterFactory mAttributeFormatterFactory;

    public GeocacheFactory() {
        mSourceFactory = new SourceFactory();
        mCacheTypeFactory = new CacheTypeFactory();
        mAttributeFormatterFactory = new AttributeFormatterFactory(new AttributeFormatterImpl(),
                new AttributeFormatterNull());
    }

    public CacheType cacheTypeFromInt(int cacheTypeIx) {
        return mCacheTypeFactory.fromInt(cacheTypeIx);
    }

    /** Mapping from cacheId to Geocache for all loaded geocaches */
    private WeakHashMap<CharSequence, Geocache> mGeocaches = 
        new WeakHashMap<CharSequence, Geocache>();

    /** @return the geocache if it is already loaded, otherwise null */
    public Geocache getFromId(CharSequence id) {
        return mGeocaches.get(id);
    }

    //TODO: This method should only be for creating a new geocache in the database
    public Geocache create(CharSequence id, CharSequence name, double latitude, double longitude,
            Source sourceType, String sourceName, CacheType cacheType, int difficulty, int terrain,
            int container) {
        if (id.length() < 2) {
            // ID is missing for waypoints imported from the browser; create a
            // new id from the time.
            id = String.format("WP%1$tk%1$tM%1$tS", System.currentTimeMillis());
        }
        if (name == null)
            name = "";
        
        Geocache cached = mGeocaches.get(id);
        if (cached != null
                && cached.getName().equals(name)
                && cached.getLatitude() == latitude
                && cached.getLongitude() == longitude
                && cached.getSourceType().equals(sourceType)
                && cached.getSourceName().equals(sourceName)
                && cached.getCacheType() == cacheType
                && cached.getDifficulty() == difficulty
                && cached.getTerrain() == terrain
                && cached.getContainer() == container)
            return cached;
                
        final AttributeFormatter attributeFormatter = mAttributeFormatterFactory
                .getAttributeFormatter(sourceType);
        cached = new Geocache(id, name, latitude, longitude, sourceType, sourceName, cacheType,
                difficulty, terrain, container, attributeFormatter);
        mGeocaches.put(id, cached);
        return cached;
    }

    public Source sourceFromInt(int sourceIx) {
        return mSourceFactory.fromInt(sourceIx);
    }

    /** Remove all cached geocache instances. Future references will reload from the database. */
    public void flushCache() {
        mGeocaches.clear();
    }
    
    public void flushCacheIcons() {
        for (Geocache geocache : mGeocaches.values()) {
            geocache.flushIcons();
        }
    }

    /** Forces the geocache to be reloaded from the database the next time it is needed. */
    public void flushGeocache(CharSequence geocacheId) {
        mGeocaches.remove(geocacheId.toString());
    }

    public Geocache fromCursor(Cursor cursor, SourceNameTranslator translator) {
        String sourceName = cursor.getString(4);

        CacheType cacheType = cacheTypeFromInt(Integer.parseInt(cursor
                .getString(5)));
        int difficulty = Integer.parseInt(cursor.getString(6));
        int terrain = Integer.parseInt(cursor.getString(7));
        int container = Integer.parseInt(cursor.getString(8));
        return create(cursor.getString(2), cursor.getString(3), cursor
                .getDouble(0), cursor.getDouble(1), translator
                .sourceNameToSourceType(sourceName), sourceName, cacheType, difficulty, terrain,
                container);
    }
    
}
