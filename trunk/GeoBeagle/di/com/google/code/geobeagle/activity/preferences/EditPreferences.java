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

package com.google.code.geobeagle.activity.preferences;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.database.filter.FilterCleanliness;

import roboguice.activity.GuicePreferenceActivity;

import android.os.Bundle;
import android.preference.Preference;

public class EditPreferences extends GuicePreferenceActivity {
    public static final String SHOW_FOUND_CACHES = "show-found-caches";
    public static final String SHOW_UNAVAILABLE_CACHES = "show-unavailable-caches";
    public static final String SHOW_WAYPOINTS = "show-waypoints";
    private FilterCleanliness filterCleanliness;
    private FilterSettingsChangeListener onPreferenceChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference showFoundCachesPreference = findPreference(SHOW_FOUND_CACHES);
        Preference showUnavailableCachesPreference = findPreference(SHOW_UNAVAILABLE_CACHES);
        Preference showWaypointsPreference = findPreference(SHOW_WAYPOINTS);
        onPreferenceChangeListener = getInjector().getInstance(
                FilterSettingsChangeListener.class);
        showWaypointsPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showFoundCachesPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showUnavailableCachesPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        filterCleanliness = getInjector().getInstance(FilterCleanliness.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Defensively set dirty bit in case we crash before onPause. This is
        // better than setting the dirty bit every time the preferences change
        // because it doesn't slow down clicking/unclicking the checkbox.
        filterCleanliness.markDirty(true);
        onPreferenceChangeListener.resetHasChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        filterCleanliness.markDirty(onPreferenceChangeListener.hasChanged());
    }

}
