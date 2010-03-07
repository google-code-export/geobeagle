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

package com.google.code.geobeagle.activity.searchonline;

import com.google.code.geobeagle.LocationControlBuffered;
import com.google.code.geobeagle.LocationControlProvider;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelper;
import com.google.code.geobeagle.activity.searchonline.JsInterface.JsInterfaceHelperFactory;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;

public class SearchOnlineModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(JsInterfaceHelperFactory.class)
                .toProvider(
                        FactoryProvider.newFactory(JsInterfaceHelperFactory.class,
                                JsInterfaceHelper.class));
        bind(LocationControlBuffered.class).toProvider(LocationControlProvider.class);
    }
}
