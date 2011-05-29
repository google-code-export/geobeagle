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
import com.google.code.geobeagle.activity.compass.fieldnotes.Toaster;
import com.google.code.geobeagle.database.filter.FilterCleanliness;
import com.google.inject.Inject;

import roboguice.activity.GuicePreferenceActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

public class EditPreferences extends GuicePreferenceActivity {
    static class SyncPreferencesChangeListener implements OnPreferenceChangeListener {
        private final SharedPreferences sharedPreferences;
        private final Toaster toaster;

        @Inject
        public SyncPreferencesChangeListener(SharedPreferences sharedPreferences, Toaster toaster) {
            this.sharedPreferences = sharedPreferences;
            this.toaster = toaster;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String otherKey = preference.getKey().equals(Preferences.BCACHING_ENABLED) ? Preferences.SDCARD_ENABLED
                    : Preferences.BCACHING_ENABLED;
            if (newValue == Boolean.FALSE && !sharedPreferences.getBoolean(otherKey, false)) {
                toaster.toast(R.string.must_have_a_sync_method, Toast.LENGTH_SHORT);
                return false;
            }
            return true;
        }
    }
    private FilterCleanliness filterCleanliness;
    private FilterSettingsChangeListener onPreferenceChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference showFoundCachesPreference = findPreference(Preferences.SHOW_FOUND_CACHES);
        Preference showDnfCachesPreference = findPreference(Preferences.SHOW_DNF_CACHES);
        Preference showUnavailableCachesPreference = findPreference(Preferences.SHOW_UNAVAILABLE_CACHES);
        Preference showWaypointsPreference = findPreference(Preferences.SHOW_WAYPOINTS);

        onPreferenceChangeListener = getInjector().getInstance(FilterSettingsChangeListener.class);
        SyncPreferencesChangeListener syncPreferencesChangeListener = getInjector().getInstance(
                SyncPreferencesChangeListener.class);
        showWaypointsPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showFoundCachesPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showDnfCachesPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        showUnavailableCachesPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        Preference sdCardEnabledPreference = findPreference(Preferences.SDCARD_ENABLED);
        Preference bcachingEnabledPreference = findPreference(Preferences.BCACHING_ENABLED);
        sdCardEnabledPreference.setOnPreferenceChangeListener(syncPreferencesChangeListener);
        bcachingEnabledPreference.setOnPreferenceChangeListener(syncPreferencesChangeListener);

        filterCleanliness = getInjector().getInstance(FilterCleanliness.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        filterCleanliness.markDirty(onPreferenceChangeListener.hasChanged());
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

}
