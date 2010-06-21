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

import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.code.geobeagle.formatting.DistanceFormatterImperial;
import com.google.code.geobeagle.formatting.DistanceFormatterMetric;
import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.SharedPreferences;

public class DistanceFormatterManagerProvider implements Provider<DistanceFormatterManager> {

    private final SharedPreferences mSharedPreferences;

    @Inject
    public DistanceFormatterManagerProvider(@DefaultSharedPreferences SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public DistanceFormatterManager get() {
        final DistanceFormatterMetric distanceFormatterMetric = new DistanceFormatterMetric();
        final DistanceFormatterImperial distanceFormatterImperial = new DistanceFormatterImperial();
        return new DistanceFormatterManager(mSharedPreferences, distanceFormatterImperial,
                distanceFormatterMetric);
    }
}
