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

import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import android.util.Log;

import java.io.File;

public class OldCacheFilesCleaner {
    public static void clean(String directory, MessageHandler messageHandler) {
        messageHandler.deletingCacheFiles();
        ExtensionFilter filter = new ExtensionFilter(".html");
        File dir = new File(directory);

        String[] list = dir.list(filter);
        File file;
        if (list.length == 0)
            return;

        for (int i = 0; i < list.length; i++) {
            file = new File(directory, list[i]);
            messageHandler.updateStatus(String.format("Deleting old cache files: [%d/%d] %s", i,
                    list.length, list[i]));
            Log.d("GeoBeagle", file + "  deleted : " + file.delete());
        }
    }
}
