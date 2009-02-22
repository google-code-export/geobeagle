
package com.google.code.geobeagle.io;

import com.google.code.geobeagle.Util;
import com.google.code.geobeagle.io.DatabaseFactory.CacheWriter;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.io.IOException;

public class LoadGpx {

    public static class Cache {
        String mId;
        double mLatitude;
        double mLongitude;
        String mName;

        public Cache(String id, String name, double latitude, double longitude) {
            mId = id;
            mName = name;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        public Cache() {
            mId = "";
            mName = "";
        }

        public String CacheToString() {
            return mLatitude + ", " + mLongitude + " (" + mId.trim() + " " + mName.trim() + ")";
        }
    }

    public static class CacheFilter {
        private final double mLatitude;
        private final double mLongitude;

        public CacheFilter(Location origin) {
            mLatitude = origin.getLatitude();
            mLongitude = origin.getLongitude();
        }

        private double distance(double lat, double lon) {
            float[] results = new float[1];
            Location.distanceBetween(mLatitude, mLongitude, lat, lon, results);
            return results[0];
        }

        public boolean filter(Cache cache) {
            return (true || distance(cache.mLatitude, cache.mLongitude) < 4000);
        }
    }

    public static class GBXmlPullParserFactory {
        public XmlPullParser create(ErrorDisplayer errorDisplayer) {
            try {
                return XmlPullParserFactory.newInstance().newPullParser();
            } catch (XmlPullParserException e) {
                errorDisplayer.displayError(e.toString() + "\n" + Util.getStackTrace(e));
                return null;
            }
        }
    }

    public static class GpxToCache {
        private Cache mCache;
        private String mCurrentTag;
        private int mEventType;
        private String mFullPath;
        private final XmlPullParser mXmlPullParser;

        public GpxToCache(XmlPullParser xmlPullParser) {
            mXmlPullParser = xmlPullParser;
            mFullPath = "";
        }

        private Cache endTag() {
            String previousFullPath = mFullPath;
            mCurrentTag = mXmlPullParser.getName();
            mFullPath = mFullPath.substring(0, mFullPath.length() - (mCurrentTag.length() + 1));
            if (previousFullPath.equals("/gpx/wpt")) {
                return mCache;
            }
            return null;
        }

        public Cache load() throws XmlPullParserException, IOException {
            mCache = new Cache();

            while (mEventType != XmlPullParser.END_DOCUMENT) {
                switch (mEventType) {
                    case XmlPullParser.START_TAG:
                        startTag();
                        break;
                    case XmlPullParser.END_TAG:
                        Cache cache = endTag();
                        if (cache != null) {
                            mEventType = mXmlPullParser.next();
                            return cache;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text();
                        break;
                }
                mEventType = mXmlPullParser.next();
            }
            return null;
        }

        public void startLoad() throws XmlPullParserException {
            mEventType = mXmlPullParser.getEventType();
        }

        private void startTag() {
            mCurrentTag = mXmlPullParser.getName();
            mFullPath += "/" + mCurrentTag;
            if (mFullPath.equals("/gpx/wpt")) {
                final String lat = mXmlPullParser.getAttributeValue(null, "lat");
                final String lon = mXmlPullParser.getAttributeValue(null, "lon");
                mCache.mLatitude = Double.parseDouble(lat);
                mCache.mLongitude = Double.parseDouble(lon);
            }
        }

        private void text() {
            final String text = mXmlPullParser.getText();
            if (mFullPath.equals("/gpx/wpt/name")) {
                mCache.mId += text;
            } else if (mFullPath.equals("/gpx/wpt/groundspeak:cache/groundspeak:name")) {
                mCache.mName += text;
            }
        }

    }

    public static LoadGpx create(Context context, ErrorDisplayer errorDisplayer,
            XmlPullParser xmlPullParser) {
        final DatabaseFactory databaseFactory = DatabaseFactory.create(context);
        final SQLiteDatabase sqlite = databaseFactory.openOrCreateCacheDatabase(errorDisplayer);
        final CacheWriter cacheWriter = databaseFactory.createCacheWriter(sqlite, errorDisplayer);
        final GpxToCache gpxToCache = new GpxToCache(xmlPullParser);
        return new LoadGpx(cacheWriter, gpxToCache);
    }

    private final CacheWriter mCacheWriter;
    private final GpxToCache mGpxToCache;

    public LoadGpx(CacheWriter cacheWriter, GpxToCache gpxToCache) {
        mCacheWriter = cacheWriter;
        mGpxToCache = gpxToCache;
    }

    public void load(CacheFilter cacheFilter) throws XmlPullParserException, IOException {
        mCacheWriter.clear();
        Cache cache;
        mCacheWriter.startWriting();
        for (mGpxToCache.startLoad(), cache = mGpxToCache.load(); cache != null; cache = mGpxToCache
                .load()) {
            if (cacheFilter.filter(cache))
                if (!mCacheWriter.write(cache.mId, cache.mName, cache.mLatitude, cache.mLongitude))
                    break;
        }
        mCacheWriter.stopWriting();
    }

}
