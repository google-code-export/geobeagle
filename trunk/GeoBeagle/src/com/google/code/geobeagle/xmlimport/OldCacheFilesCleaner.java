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


import com.google.inject.Inject;

import android.util.Log;

import java.io.File;

public class OldCacheFilesCleaner {
    private final String directory;

    @Inject
    public OldCacheFilesCleaner(GeoBeagleEnvironment geoBeagleEnvironment) {
        this.directory = geoBeagleEnvironment.getOldDetailsDirectory();
    }

    public void clean(MessageHandlerInterface messageHandler) {
        messageHandler.deletingCacheFiles();
        String[] list = new File(directory).list(new ExtensionFilter(".html"));
        if (list == null)
            return;
        for (int i = 0; i < list.length; i++) {
            messageHandler.updateStatus(String.format("Deleting old cache files: [%d/%d] %s", i,
                    list.length, list[i]));
            File file = new File(directory, list[i]);
            Log.d("GeoBeagle", file + "  deleted : " + file.delete());
        }
    }
}
