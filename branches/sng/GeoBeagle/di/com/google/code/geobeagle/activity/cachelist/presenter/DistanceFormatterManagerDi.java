package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DistanceFormatterManagerDi {

    public static DistanceFormatterManager create(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    
        final DistanceFormatterMetric distanceFormatterMetric = new DistanceFormatterMetric();
        final DistanceFormatterImperial distanceFormatterImperial = new DistanceFormatterImperial();
        return new DistanceFormatterManager(sharedPreferences, distanceFormatterImperial,
                distanceFormatterMetric);
    }
}
