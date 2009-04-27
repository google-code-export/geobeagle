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

package com.google.code.geobeagle.data;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Geocache or letterbox description, id, and coordinates.
 *
 * This class holds a bunch of Robert's changes which aren't yet user-visible.
 */

public class Geocache2 implements Parcelable {
	public static enum Provider {
		ATLAS_QUEST(0), GROUNDSPEAK(1), MY_LOCATION(-1);

		private final int mIx;

		Provider(int ix) {
			mIx = ix;
		}

		public int toInt() {
			return mIx;
		}
	}

	public static enum Source {
		GPX(0), MY_LOCATION(1), WEB_URL(2);

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

	public static Parcelable.Creator<Geocache> CREATOR = new GeocacheFactory.CreateGeocacheFromParcel();
	public static final String ID = "id";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String NAME = "name";

	public static final String SOURCE_NAME = "sourceName";
	public static final String SOURCE_TYPE = "sourceType";
	private CharSequence mCacheType;
	private CharSequence mContainer;

	private int mContentSelectorIndex;
	private CharSequence mDiff, mTerr;
	private CharSequence mId;
	private boolean mIsArchived;
	private boolean mIsAvailable;
	private double mLatitude;
	private CharSequence mLogBlob;

	private boolean mLongDescIsHtml;
	/*
	 * TODO: private final CharSequence mPlacer; private final CharSequence mOwner;
	 * TODO: private final int mOwner_id; private final Date mPlacedDate;
	 */
	private double mLongitude;
	private CharSequence mName;
	private CharSequence mPlacementDate;
	private CharSequence mPlacer;
	private CharSequence mShortDesc, mLongDesc, mHint;
	private boolean mShortDescIsHtml;
	private String mSourceName;
	private Source mSourceType;


	public Geocache() {
	}

	public Provider getContentProvider() {
        // Must use toString() rather than mId.subSequence(0,2).equals("GC"),
        // because editing the text in android produces a SpannableString rather
        // than a String, so the CharSequences won't be equal.
        String prefix = mId.subSequence(0, 2).toString();
        if (prefix.equals("GC"))
            return Provider.GROUNDSPEAK;
        if (prefix.equals("LB"))
            return Provider.ATLAS_QUEST;
        else
            return Provider.MY_LOCATION;
    }

	public Geocache(CharSequence id, CharSequence name, double latitude,
			double longitude, Source sourceType, String sourceName) {
		mId = id;
		mName = name;
		mLatitude = latitude;
		mLongitude = longitude;
		mSourceType = sourceType;
		mSourceName = sourceName;
		mIsAvailable = true;
	}

	public CharSequence asHtml() {
		// TODO: do a search and replace on the template version from the
		// resource.
		String h = "<b>" + mId + "</b> - " + getName() + "<br >";
		h += "Placed " + mPlacementDate + " by " + mPlacer + "<br >";
		h += mDiff + "/" + mTerr + " " + mCacheType + "/" + mContainer
				+ "<p />";
		h += mShortDesc + "<br />" + mLongDesc + "<hr />" + mHint + "hr />"
				+ mLogBlob;

		return h;
	}

	public int describeContents() {
		return 0;
	}

	public CharSequence getCacheType() {
		return mCacheType;
	}

	public CharSequence getContainer() {
		return mContainer;
	}

	public CharSequence getDiff() {
		return mDiff;
	}

	public CharSequence getHint() {
		return mHint;
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

	public CharSequence getLogBlob() {
		return mLogBlob;
	}

	// Always returned as HTML.
	public CharSequence getLongDesc() {
		return mLongDesc;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public CharSequence getName() {
		if (mIsArchived) {
			return "<font color='red'><strike>" + mName + "</strike></font>";
		}
		if (!mIsAvailable) {
			return "<strike>" + mName + "</strike>";
		}
		return mName;
	}

	public CharSequence getNamePlain() {
		return mName;
	}

	public CharSequence getPlacementDay() {
		return mPlacementDate;
	}

	public CharSequence getPlacer() {
		return mPlacer;
	}

	// Always returned as HTML.
	public CharSequence getShortDesc() {
		return mShortDesc;
	}

	public CharSequence getShortId() {
		if (mId.length() > 2)
			return mId.subSequence(2, mId.length());
		else
			return "";
	}

	public String getSourceName() {
		return mSourceName;
	}

	public Source getSourceType() {
		return mSourceType;
	}

	public CharSequence getTerr() {
		return mTerr;
	}

	public boolean isArchived() {
		return mIsArchived;
	}

	public boolean isAvailable() {
		return mIsAvailable;
	}

	public void setCacheType(CharSequence cacheType) {
		mCacheType = cacheType;
	}

	public void setContainer(CharSequence container) {
		mContainer = container;
	}

	public void setCoords(double latitude, double longitude) {
		mLatitude = latitude;
		mLongitude = longitude;
	}

	public void setDiff(CharSequence difficulty) {
		mDiff = difficulty;
	}

	public void setHint(CharSequence hint) {
		mHint = hint;
	}

	public void setId(CharSequence id) {
		mId = id;
	}

	public void setLogBlob(CharSequence logBlob) {
		mLogBlob = logBlob;
	}

	public void setLongDesc(CharSequence desc, boolean isHtml) {
		mLongDesc = desc;
		mLongDescIsHtml = isHtml;
	}

	public void setName(CharSequence name) {
		mName = name;
	}

	public void setPlacementDate(CharSequence placementDate) {
		// TODO - if we were ever going to do anything with this date other than
		// display
		// it (maybe we want to reformat it to user prefs) then mPlacementDate
		// should be
		// a Date and it should do "real" Date parsing and such. For now, punt.
		mPlacementDate = placementDate.subSequence(0, 10);
	}

	public void setPlacer(CharSequence placer) {
		mPlacer = placer;
	}

	public void setShortDesc(CharSequence desc, boolean isHtml) {
		mShortDesc = desc;
		mShortDescIsHtml = isHtml;
	}

	public void setStatus(boolean available, boolean archived) {
		mIsAvailable = available;
		mIsArchived = archived;
	}

	public void setTerr(CharSequence terrain) {
		mTerr = terrain;
	}

	public void writeToParcel(Parcel out, int flags) {
		Bundle bundle = new Bundle();
		bundle.putCharSequence(ID, mId);
		bundle.putCharSequence(NAME, mName);
		bundle.putDouble(LATITUDE, mLatitude);
		bundle.putDouble(LONGITUDE, mLongitude);
		bundle.putInt(SOURCE_TYPE, mSourceType.mIx);
		bundle.putString(SOURCE_NAME, mSourceName);
		out.writeBundle(bundle);
	}

	public void writeToPrefs(Editor editor) {
		editor.putString(ID, mId.toString());
		editor.putString(NAME, mName.toString());
		editor.putFloat(LATITUDE, (float) mLatitude);
		editor.putFloat(LONGITUDE, (float) mLongitude);
		editor.putInt(SOURCE_TYPE, mSourceType.mIx);
		editor.putString(SOURCE_NAME, mSourceName);
	}
}
