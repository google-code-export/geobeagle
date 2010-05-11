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

import com.google.code.geobeagle.activity.main.GeoBeagleModule.DefaultSharedPreferences;
import com.google.code.geobeagle.bcaching.ImportBCachingWorker.ImportBCachingWorkerFactory;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import android.content.SharedPreferences;

import roboguice.config.AbstractAndroidModule;

import java.util.Hashtable;

public class BCachingModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(ImportBCachingWorkerFactory.class).toProvider(
                FactoryProvider.newFactory(ImportBCachingWorkerFactory.class,
                        ImportBCachingWorker.class));
    }

    @Provides
    BCachingCommunication bcachingCommunicationProvider(
            @DefaultSharedPreferences SharedPreferences sharedPreferences) {
        String bcachingUsername = sharedPreferences.getString("bcaching-username", "");
        String bcachingPassword = sharedPreferences.getString("bcaching-password", "");
        return new BCachingCommunication(bcachingUsername, bcachingPassword);
    }

    @Provides
    Hashtable<String, String> getCacheListParamsProvider() {
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("a", "list");
        params.put("found", "0");
        params.put("app", "GeoBeagle");
        return params;
    }
}
