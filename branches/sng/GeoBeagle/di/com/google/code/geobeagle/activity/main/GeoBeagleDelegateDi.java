package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuAction;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionCacheList;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionEditGeocache;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionLogDnf;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionLogFind;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionSearchOnline;
import com.google.code.geobeagle.activity.main.MenuActions.MenuActionSettings;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSenderDI;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.res.Resources;
import android.view.LayoutInflater;

import java.util.HashMap;

public class GeoBeagleDelegateDi {

    public static GeoBeagleDelegate createGeoBeagleDelegate(GeoBeagle parent,
            AppLifecycleManager appLifecycleManager, GeocacheViewer geocacheViewer,
            ErrorDisplayer errorDisplayer) {
        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(parent);
        final LayoutInflater layoutInflater = LayoutInflater.from(parent);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc
                .createCacheDetailsOnClickListener(parent, cacheDetailsBuilder, geocacheViewer,
                        errorDisplayer, layoutInflater);
        final FieldNoteSender fieldNoteSender = FieldNoteSenderDI.build(parent, layoutInflater);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(parent);
        final HashMap<Integer, MenuAction> menuActions = new HashMap<Integer, MenuAction>();
        final Resources resources = parent.getResources();
    
        menuActions.put(R.id.menu_cache_list, new MenuActions.MenuActionCacheList(parent));
        menuActions.put(R.id.menu_edit_geocache, new MenuActions.MenuActionEditGeocache(parent));
        menuActions.put(R.id.menu_log_dnf, new MenuActions.MenuActionLogDnf(parent));
        menuActions.put(R.id.menu_log_find, new MenuActions.MenuActionLogFind(parent));
        menuActions.put(R.id.menu_search_online, new MenuActions.MenuActionSearchOnline(parent));
        menuActions.put(R.id.menu_settings, new MenuActions.MenuActionSettings(parent));
        return new GeoBeagleDelegate(parent, activitySaver, appLifecycleManager,
                cacheDetailsOnClickListener, fieldNoteSender, menuActions, resources);
    }

}
