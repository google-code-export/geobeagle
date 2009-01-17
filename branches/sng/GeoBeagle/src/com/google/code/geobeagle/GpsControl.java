package com.google.code.geobeagle;

import android.location.Location;
import android.location.LocationManager;

public class GpsControl {
	/**
     * 
     *
     */
	public static class LocationChooser {

		/**
		 * Choose the better of two locations:
		 * 
		 * If one location is newer and more accurate, choose that. (This favors
		 * the gps).
		 * 
		 * Otherwise, if one location is newer, less accurate, but farther away
		 * than the sum of the two accuracies, choose that. (This favors the
		 * network locator if you've driven a distance and haven't been able to
		 * get a gps fix yet.)
		 */
		public Location choose(Location location1, Location location2) {
			if (location1 == null)
				return location2;
			if (location2 == null)
				return location1;

			if (location2.getTime() > location1.getTime()) {
				if (location2.getAccuracy() <= location1.getAccuracy())
					return location2;
				else {
					if (location1.distanceTo(location2) >= location1
							.getAccuracy()
							+ location2.getAccuracy())
						return location2;
				}
			}
			return location1;
		}
	}

	private final LocationChooser mLocationChooser;
	private final LocationManager mLocationManager;

	public GpsControl(LocationManager locationManager,
			LocationChooser locationChooser) {
		this.mLocationManager = locationManager;
		this.mLocationChooser = locationChooser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.geobrowse.GpsControlI#getLocation(android.content.Context)
	 */
	public Location getLocation() {
		return mLocationChooser
				.choose(
						mLocationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER),
						mLocationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
	}

}
