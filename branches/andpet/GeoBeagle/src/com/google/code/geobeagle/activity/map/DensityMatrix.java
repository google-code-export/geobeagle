package com.google.code.geobeagle.activity.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.android.maps.GeoPoint;
import com.google.code.geobeagle.Geocache;

public class DensityMatrix {

	private double mLatResolution;
	private double mLonResolution;

	public class DensityPatch {
		private int mCacheCount;
		private double mLatLow;
		private double mLonLow;
		public DensityPatch(double latLow, double lonLow) {
			mCacheCount = 0;
			mLatLow = latLow;
			mLonLow = lonLow;
		}
		public int getCacheCount() { return mCacheCount; }
		public void setCacheCount(int count) { mCacheCount = count; }
		public GeoPoint getExtentLow() {
			return new GeoPoint((int)(mLatLow*1E6), (int)(mLonLow*1E6));
		}
		public GeoPoint getExtentHigh() {
			return new GeoPoint((int)((mLatLow+mLatResolution)*1E6), 
			                    (int)((mLonLow+mLonResolution)*1E6));
		}
	}
	
	/** Mapping lat -> (mapping lon -> cache count) */
	private TreeMap<Integer, Map<Integer, DensityPatch>> mBuckets =
		new TreeMap<Integer, Map<Integer, DensityPatch>>();

	private void incrementBucket(double lat, double lon) {
		Integer latBucket = (int)Math.floor(lat / mLatResolution);
		Integer lonBucket = (int)Math.floor(lon / mLonResolution);
		Map<Integer, DensityPatch> lonMap = mBuckets.get(latBucket);
		if (lonMap == null) {
			//Key didn't exist in map
			lonMap = new TreeMap<Integer, DensityPatch>();
			mBuckets.put(latBucket, lonMap);
		}

		DensityPatch patch = lonMap.get(lonBucket);
		if (patch == null) {
			patch = new DensityPatch(latBucket * mLatResolution, 
			                         lonBucket * mLonResolution);
			lonMap.put(lonBucket, patch);
		}

		patch.setCacheCount(patch.getCacheCount() + 1);
	}
	
	public DensityMatrix(double latResolution,
	                     double lonResolution) {
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
}
