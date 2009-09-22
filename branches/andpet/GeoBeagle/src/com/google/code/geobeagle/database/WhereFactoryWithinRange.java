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

public class WhereFactoryWithinRange implements WhereFactory {
	private double mSpanLat;
	private double mSpanLon;

	public WhereFactoryWithinRange(double spanLat, double spanLon) {
		mSpanLat = spanLat;
		mSpanLon = spanLon;
    }

    @Override
    public String getWhere(ISQLiteDatabase sqliteWrapper, double latitude, double longitude) {

        double latLow = latitude - mSpanLat/2.0;
        double latHigh = latitude + mSpanLat/2.0;
        double lonLow = longitude - mSpanLon/2.0;
        double lonHigh = longitude + mSpanLon/2.0;

        return "Latitude > " + latLow + " AND Latitude < " + latHigh + 
               " AND Longitude > " + lonLow + " AND Longitude < " + lonHigh;
    }
}