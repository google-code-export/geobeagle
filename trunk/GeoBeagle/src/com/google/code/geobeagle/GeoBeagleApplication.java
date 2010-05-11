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

package com.google.code.geobeagle;

import com.google.code.geobeagle.activity.cachelist.CacheListModule;
import com.google.code.geobeagle.activity.cachelist.model.ModelModule;
import com.google.code.geobeagle.activity.main.GeoBeagleModule;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnotesModule;
import com.google.code.geobeagle.activity.main.view.ViewModule;
import com.google.code.geobeagle.activity.map.GeoMapActivityModule;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineModule;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.database.DatabaseModule;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule;
import com.google.code.geobeagle.location.LocationModule;
import com.google.code.geobeagle.xmlimport.XmlimportModule;
import com.google.inject.Module;

import roboguice.application.GuiceApplication;

import java.util.List;

public class GeoBeagleApplication extends GuiceApplication {
    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new SearchOnlineModule());
        modules.add(new FieldnotesModule());
        modules.add(new GeoMapActivityModule());
        modules.add(new GeoBeagleModule());
        modules.add(new GeoBeaglePackageModule());
        modules.add(new ViewModule());
        modules.add(new DatabaseModule());
        modules.add(new CacheListModule());
        modules.add(new LocationModule());
        modules.add(new ModelModule());
        modules.add(new GpsStatusWidgetModule());
        modules.add(new XmlimportModule());
        modules.add(new BCachingModule());
    }
}
