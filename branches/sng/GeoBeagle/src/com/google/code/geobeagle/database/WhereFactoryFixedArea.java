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

package com.google.code.geobeagle.database;

/** Where clause with limits set during construction, not when calling getWhere() */
public class WhereFactoryFixedArea implements WhereFactory {
	private double mLatLow;
	private double mLonLow;
	private double mLatHigh;
	private double mLonHigh;

	public WhereFactoryFixedArea(double latLow, double lonLow,
	                             double latHigh, double lonHigh) {
		mLatLow = Math.min(latLow, latHigh);
		mLonLow = Math.min(lonLow, lonHigh);
		mLatHigh = Math.max(latLow, latHigh);
		mLonHigh = Math.max(lonLow, lonHigh);
	}
	
	@Override
	public String getWhere(ISQLiteDatabase sqliteWrapper, 
	                       double latitude, double longitude) {

		return "Latitude >= " + mLatLow + " AND Latitude < " + mLatHigh + 
		       " AND Longitude >= " + mLonLow + " AND Longitude < " + mLonHigh;
		
		// + " AND (CacheType = 1 OR CacheType = 2 OR CacheType = 3 OR CacheType = 4)";
	}
}