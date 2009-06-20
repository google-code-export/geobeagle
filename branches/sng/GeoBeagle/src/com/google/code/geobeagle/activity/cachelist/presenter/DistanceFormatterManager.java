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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.formatting.DistanceFormatter;
import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class DistanceFormatterManager {
    private ArrayList<HasDistanceFormatter> mHasDistanceFormatters;
    private final DistanceFormatterMetric mDistanceFormatterMetric;
    private final DistanceFormatterImperial mDistanceFormatterImperial;
    private final SharedPreferences mDefaultSharedPreferences;

    public DistanceFormatterManager(SharedPreferences defaultSharedPreferences,
            DistanceFormatterImperial distanceFormatterImperial,
            DistanceFormatterMetric distanceFormatterMetric) {
        mDistanceFormatterImperial = distanceFormatterImperial;
        mDistanceFormatterMetric = distanceFormatterMetric;
        mDefaultSharedPreferences = defaultSharedPreferences;
        mHasDistanceFormatters = new ArrayList<HasDistanceFormatter>(2);
    }

    public void addHasDistanceFormatter(HasDistanceFormatter hasDistanceFormatter) {
        mHasDistanceFormatters.add(hasDistanceFormatter);
    }

    public void setFormatter() {
        final boolean imperial = mDefaultSharedPreferences.getBoolean("imperial", false);
        for (HasDistanceFormatter hasDistanceFormatter : mHasDistanceFormatters) {
            final DistanceFormatter distanceFormatter = imperial ? mDistanceFormatterImperial
                    : mDistanceFormatterMetric;
            hasDistanceFormatter.setDistanceFormatter(distanceFormatter);
        }
    }
}
