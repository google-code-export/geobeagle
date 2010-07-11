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

package com.google.code.geobeagle.activity.map;

import com.google.android.maps.Overlay;
import com.google.code.geobeagle.Geocache;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

public class GeoMapActivityModule extends AbstractAndroidModule {

    static class NullOverlay extends Overlay {
    }

    @Override
    protected void configure() {
    }

    @Provides
    CachePinsOverlay providesCachePinsOverlay(CacheItemFactory cacheItemFactory, Context context,
            Resources resources) {
        return new CachePinsOverlay(resources, cacheItemFactory, context, new ArrayList<Geocache>());
    }
}
