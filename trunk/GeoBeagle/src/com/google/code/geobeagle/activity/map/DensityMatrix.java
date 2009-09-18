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

package com.google.code.geobeagle.activity.map;

import com.google.code.geobeagle.Geocache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DensityMatrix {

    public static class DensityPatch {
        private int mCacheCount;
        private final int mLatLowE6;
        private final int mLonLowE6;

        public DensityPatch(double latLow, double lonLow) {
            mCacheCount = 0;
            mLatLowE6 = (int)(latLow * 1E6);
            mLonLowE6 = (int)(lonLow * 1E6);
        }

        public int getAlpha() {
            return Math.min(210, 10 + 32 * mCacheCount);
        }

        public int getCacheCount() {
            return mCacheCount;
        }

        public int getLatLowE6() {
            return mLatLowE6;
        }

        public int getLonLowE6() {
            return mLonLowE6;
        }

        public void setCacheCount(int count) {
            mCacheCount = count;
        }
    }

    /** Mapping lat -> (mapping lon -> cache count) */
    private TreeMap<Integer, Map<Integer, DensityPatch>> mBuckets = new TreeMap<Integer, Map<Integer, DensityPatch>>();
    private double mLatResolution;
    private double mLonResolution;

    public DensityMatrix(double latResolution, double lonResolution) {
        mLatResolution = latResolution;
        mLonResolution = lonResolution;
    }

    public void addCaches(ArrayList<Geocache> list) {
        for (Geocache cache : list) {
            incrementBucket(cache.getLatitude(), cache.getLongitude());
        }
    }

    public List<DensityPatch> getDensityPatches() {
        ArrayList<DensityPatch> result = new ArrayList<DensityPatch>();
        for (Integer latBucket : mBuckets.keySet()) {
            Map<Integer, DensityPatch> lonMap = mBuckets.get(latBucket);
            result.addAll(lonMap.values());
        }
        return result;
    }

    private void incrementBucket(double lat, double lon) {
        Integer latBucket = (int)Math.floor(lat / mLatResolution);
        Integer lonBucket = (int)Math.floor(lon / mLonResolution);
        Map<Integer, DensityPatch> lonMap = mBuckets.get(latBucket);
        if (lonMap == null) {
            // Key didn't exist in map
            lonMap = new TreeMap<Integer, DensityPatch>();
            mBuckets.put(latBucket, lonMap);
        }

        DensityPatch patch = lonMap.get(lonBucket);
        if (patch == null) {
            patch = new DensityPatch(latBucket * mLatResolution, lonBucket * mLonResolution);
            lonMap.put(lonBucket, patch);
        }

        patch.setCacheCount(patch.getCacheCount() + 1);
    }
}
