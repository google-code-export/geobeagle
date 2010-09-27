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

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.CacheTypeFactory;
import com.google.code.geobeagle.database.CacheWriter;
import com.google.code.geobeagle.database.ClearCachesFromSource;
import com.google.code.geobeagle.database.GpxWriter;
import com.google.code.geobeagle.database.TagWriter;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;
import com.google.inject.Provider;

public class CachePersisterFacadeFactory {

    public ImportCacheActions create(CacheWriter cacheWriter,
            GpxWriter gpxWriter,
            ImportWakeLock importWakeLock,
            GeoBeagleEnvironment geoBeagleEnvironment,
            Provider<MessageHandler> messageHandlerProvider,
            TagWriter tagWriter,
            ClearCachesFromSource clearCachesFromSource,
            CacheTypeFactory cacheTypeFactory) {
        final CacheTagSqlWriter cacheTagSqlWriter = new CacheTagSqlWriter(cacheWriter, gpxWriter,
                cacheTypeFactory, tagWriter, clearCachesFromSource);
        return new ImportCacheActions(cacheTagSqlWriter, messageHandlerProvider.get(),
                importWakeLock, geoBeagleEnvironment);
    }
}
