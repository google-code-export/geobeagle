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
import com.google.code.geobeagle.activity.compass.CompassActivityModule;
import com.google.code.geobeagle.activity.map.click.MapModule;
import com.google.code.geobeagle.bcaching.BCachingModule;
import com.google.code.geobeagle.database.DatabaseModule;
import com.google.code.geobeagle.gpsstatuswidget.GpsStatusWidgetModule;
import com.google.code.geobeagle.location.LocationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import roboguice.application.GuiceApplication;
import roboguice.config.AbstractAndroidModule;

import java.util.ArrayList;
import java.util.List;

public class GeoBeagleApplication extends GuiceApplication {

    public static Timing timing = new Timing();

    @Override
    protected Injector createInjector() {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module roboguiceModule = new FasterRoboGuiceModule(contextScope, throwingContextProvider,
                contextProvider, resourceListener, viewListener, extrasListener, this);
        modules.add(roboguiceModule);
        addApplicationModules(modules);
        for (Module m : modules) {
            if (m instanceof AbstractAndroidModule) {
                ((AbstractAndroidModule)m).setStaticTypeListeners(staticTypeListeners);
            }
        }
        return Guice.createInjector(Stage.DEVELOPMENT, modules);
    }


    @Override
    protected void addApplicationModules(List<Module> modules) {
        timing.start();
//        Debug.startMethodTracing("dmtrace", 32 * 1024 * 1024);
        modules.add(new CompassActivityModule());      // +1 second (11.0)
        modules.add(new GeoBeaglePackageModule());
        modules.add(new DatabaseModule());
        modules.add(new CacheListModule());
        modules.add(new LocationModule());
        modules.add(new ModelModule());
        modules.add(new GpsStatusWidgetModule());
        modules.add(new BCachingModule());
        modules.add(new MapModule());
    }
}
