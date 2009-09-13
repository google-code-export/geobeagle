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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextAction;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionDelete;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionView;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuActionSyncGpx;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.activity.cachelist.presenter.TitleUpdater;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.DatabaseDI;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.code.geobeagle.database.ISQLiteDatabase;

//TODO: Remove this class
public class GeocacheListControllerFactory {
    private final ContextActionDeleteFactory mContextActionDeleteFactory;
    private final ContextActionView mContextActionView;
    private final FilterNearestCaches mFilterNearestCaches;
    private final MenuActions mMenuActions;
    private final ContextActionEdit mContextActionEdit;
    MenuActionSyncGpx mMenuActionSyncGpx;

    public GeocacheListControllerFactory(ContextActionDeleteFactory contextActionDeleteFactory,
            ContextActionEdit contextActionEdit, ContextActionView contextActionView,
            FilterNearestCaches filterNearestCaches,
            MenuActions menuActions, MenuActionSyncGpx menuActionSyncGpx) {
        mFilterNearestCaches = filterNearestCaches;
        mContextActionEdit = contextActionEdit;
        mContextActionView = contextActionView;
        mMenuActions = menuActions;
        mMenuActionSyncGpx = menuActionSyncGpx;
        mContextActionDeleteFactory = contextActionDeleteFactory;
    }

    public IGeocacheListController create(CacheListRefresh cacheListRefresh,
            TitleUpdater titleUpdater, ISQLiteDatabase writableDatabase) {
        final CacheWriter cacheWriter = DatabaseDI.createCacheWriter(writableDatabase);
        final ContextActionDelete contextActionDelete = mContextActionDeleteFactory.create(
                titleUpdater, cacheWriter);

        final ContextAction[] contextActions = new ContextAction[] {
                contextActionDelete, mContextActionView, mContextActionEdit
        };
        return new GeocacheListController(cacheListRefresh, contextActions, mFilterNearestCaches,
                mMenuActionSyncGpx, mMenuActions);
    }
}
