
package com.google.code.geobeagle.data;

import android.os.Bundle;
import android.os.Parcel;

public class GeocacheFromParcelFactory {
    private final GeocacheFactory mGeocacheFactory;

    public GeocacheFromParcelFactory(GeocacheFactory geocacheFactory) {
        mGeocacheFactory = geocacheFactory;
    }

    public Geocache create(Parcel in) {
        Bundle bundle = in.readBundle();
        return mGeocacheFactory.create(bundle.getCharSequence("id"),
                bundle.getCharSequence("name"), bundle.getDouble("latitude"), bundle
                        .getDouble("longitude"), mGeocacheFactory.sourceFromInt(bundle
                        .getInt("sourceType")), bundle.getString("sourceName"));

    }
}
