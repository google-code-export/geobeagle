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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.database.ClearCachesFromSource;
import com.google.code.geobeagle.database.ClearCachesFromSourceNull;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.util.RoboThread;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;


public class BCachingModule extends AbstractAndroidModule {

    public static final String BCACHING_INITIAL_MESSAGE = "Getting cache count...";
    @Provides
    WakeLock wakeLockProvider(PowerManager powerManager) {
        return powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
    }

    @Override
    protected void configure() {
        bind(ClearCachesFromSource.class).to(ClearCachesFromSourceNull.class);
        
        requestStaticInjection(RoboThread.class);
        
    }
}
