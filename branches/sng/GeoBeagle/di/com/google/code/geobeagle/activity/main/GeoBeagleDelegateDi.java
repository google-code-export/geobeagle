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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSenderDI;
import com.google.code.geobeagle.activity.main.intents.IntentStarterViewUri;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionCacheList;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionEditGeocache;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionLogDnf;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionLogFind;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionSearchOnline;
import com.google.code.geobeagle.activity.main.menuactions.MenuActionSettings;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import java.util.HashMap;

public class GeoBeagleDelegateDi {

    public static GeoBeagleDelegate createGeoBeagleDelegate(GeoBeagle parent,
            AppLifecycleManager appLifecycleManager, GeocacheViewer geocacheViewer,
            ErrorDisplayer errorDisplayer, IntentStarterViewUri intentStarterViewGoogleMaps,
            RadarView radar) {
        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(parent);
        final LayoutInflater layoutInflater = LayoutInflater.from(parent);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc
                .createCacheDetailsOnClickListener(parent, cacheDetailsBuilder, geocacheViewer,
                        errorDisplayer, layoutInflater);
        final FieldNoteSender fieldNoteSender = FieldNoteSenderDI.build(parent, layoutInflater);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(parent);
        final HashMap<Integer, MenuAction> menuActions = new HashMap<Integer, MenuAction>();
        final Resources resources = parent.getResources();

        menuActions.put(R.id.menu_cache_list, new MenuActionCacheList(parent));
        menuActions.put(R.id.menu_edit_geocache, new MenuActionEditGeocache(parent));
        menuActions.put(R.id.menu_log_dnf, new MenuActionLogDnf(parent));
        menuActions.put(R.id.menu_log_find, new MenuActionLogFind(parent));
        menuActions.put(R.id.menu_search_online, new MenuActionSearchOnline(parent));
        menuActions.put(R.id.menu_settings, new MenuActionSettings(parent));
        menuActions.put(R.id.menu_google_maps,
                new MenuActionGoogleMaps(intentStarterViewGoogleMaps));
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(parent);
        return new GeoBeagleDelegate(parent, activitySaver, appLifecycleManager,
                cacheDetailsOnClickListener, fieldNoteSender, menuActions, resources,
                defaultSharedPreferences, radar);
    }

}
