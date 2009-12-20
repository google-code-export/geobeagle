package com.google.code.geobeagle.database;

import com.google.code.geobeagle.CacheFilter;
import com.google.code.geobeagle.GeocacheList;
import com.google.code.geobeagle.activity.main.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Uses a DB to fetch the caches within a defined region, or all caches if no 
 * bounds were specified */
public class CachesProviderDb implements ICachesProviderArea {

    private DbFrontend mDbFrontend;
    private double mLatLow;
    private double mLonLow;
    private double mLatHigh;
    private double mLonHigh;
    /** The complete SQL query for the current coordinates and settings,
     *  except from 'SELECT x'. If null, mSql needs to be re-calculated. */
    private String mSql = null;
    private GeocacheList mCaches;
    private boolean mHasChanged = true;
    private boolean mHasLimits = false;
    /** The 'where' part of the SQL clause that comes from the active CacheFilter */ 
    private String mFilter = "";
    /** The tag used for filtering. Tags.NULL means any tag allowed. */
    private Set<Integer> mRequiredTags = new HashSet<Integer>();
    private Set<Integer> mForbiddenTags = new HashSet<Integer>();
    /** If greater than zero, this is the max number that mCaches 
     * was allowed to contain when loaded. (This limit can change on subsequent loads) */
    private int mCachesCappedToCount = 0;

    public CachesProviderDb(DbFrontend dbFrontend) {
        mDbFrontend = dbFrontend;
    }

    /** @param start is prepended if there are any parts
     */
     /*
    private static String BuildConjunction(String start, List<String> parts) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (String part : parts) {
            if (first) {
                buffer.append(start);
                first = false;
            } else {
                buffer.append(" AND ");
            }
            buffer.append(part);
        }
        return buffer.toString();
    }
*/
    
    /** Returns a complete SQL query except from 'SELECT x' */
    private String getSql() {
        if (mSql != null)
            return mSql;

        ArrayList<String> where = new ArrayList<String>(4);
        if (mHasLimits) {
            where.add("Latitude >= " + mLatLow + " AND Latitude < " + mLatHigh + 
                    " AND Longitude >= " + mLonLow + " AND Longitude < " + mLonHigh);
        }

        if (!mFilter.equals(""))
            where.add(mFilter);

        String join = "";
        if (mForbiddenTags.size() > 0) {
            StringBuffer forbidden = new StringBuffer();
            boolean first = true;
            for (Integer tagId : mForbiddenTags) {
                if (first) {
                    first = false;
                } else {
                    forbidden.append(" or ");
                }
                forbidden.append("TagId=" + tagId);
            }
            join = " left outer join (select CacheId from cachetags where "
                + forbidden + ") as FoundTags on caches.Id = FoundTags.CacheId";
            where.add("FoundTags.CacheId is NULL");
        }
        
        StringBuffer tables = new StringBuffer("FROM CACHES");
        int ix = 1;
        for (Integer tagId : mRequiredTags) {
            String table = "tags" + ix;
            tables.append(", CACHETAGS " + table);
            where.add(table+".TagId=" + tagId + " AND " + table + ".CacheId=Id");
            ix++;
        }
        
        StringBuffer completeSql = tables;  //new StringBuffer(tables);
        completeSql.append(join);
        boolean first = true;
        for (String part : where) {
            if (first) {
                completeSql.append(" WHERE ");
                first = false;
            } else {
                completeSql.append(" AND ");
            }
            completeSql.append(part);
        }
        
        mSql = completeSql.toString();        
        //Log.d("GeoBeagle", "CachesProviderDb created sql " + mSql);
        return mSql;
    }
    
    @Override
    public GeocacheList getCaches() {
        if (mCaches == null || mCachesCappedToCount > 0) {
            mCaches = mDbFrontend.loadCachesRaw("SELECT Id " + getSql());
        }
        return mCaches;
    }

    @Override
    public GeocacheList getCaches(int maxResults) {
        if (mCaches == null || (mCachesCappedToCount > 0 && 
                                mCachesCappedToCount < maxResults)) {
            mCaches = mDbFrontend.loadCachesRaw("SELECT Id " + getSql() + " LIMIT 0, " + maxResults);
            if (mCaches.size() == maxResults)
                mCachesCappedToCount = maxResults;
            else
                //The cap didn't limit the search result
                mCachesCappedToCount = 0;
        }
        return mCaches;
    }
    
    @Override
    public int getCount() {
        if (mCaches == null || mCachesCappedToCount > 0) {
            return mDbFrontend.countRaw("SELECT COUNT(*) " + getSql());
        }
        return mCaches.size();
    }

    @Override
    public void setBounds(double latLow, double lonLow, double latHigh, double lonHigh) {
        if (Util.approxEquals(latLow, mLatLow) 
                && Util.approxEquals(latHigh, mLatHigh) 
                && Util.approxEquals(lonLow, mLonLow)
                && Util.approxEquals(lonHigh, mLonHigh)) {
            return;
        }
        mLatLow = latLow;
        mLatHigh = latHigh;
        mLonLow = lonLow;
        mLonHigh = lonHigh;
        mCaches = null;  //Flush old caches
        mSql = null;
        mHasChanged = true;
        mHasLimits = true;
    }

    @Override
    public boolean hasChanged() {
        return mHasChanged;
    }

    @Override
    public void resetChanged() {
        mHasChanged = false;
    }

    /** Tells this class that the contents in the database have changed. 
     * The cached list isn't reliable any more. */
    public void notifyOfDbChange() {
        mCaches = null;
        mHasChanged = true;
    }
    
    public void setFilter(CacheFilter cacheFilter) {
        String newFilter = cacheFilter.getSqlWhereClause();
        Set<Integer> newTags = cacheFilter.getRequiredTags();
        Set<Integer> newForbiddenTags = cacheFilter.getForbiddenTags();
        
        if (newFilter.equals(mFilter) && newTags.equals(mRequiredTags) &&
                newForbiddenTags.equals(mForbiddenTags))
            return;
        
        mHasChanged = true;
        mFilter = newFilter;
        mRequiredTags = newTags;
        mForbiddenTags = newForbiddenTags;
        mSql = null;   //Flush
        mCaches = null;  //Flush old caches
    }
    
    public int getTotalCount() {
        return mDbFrontend.count(mFilter);
    }
}
