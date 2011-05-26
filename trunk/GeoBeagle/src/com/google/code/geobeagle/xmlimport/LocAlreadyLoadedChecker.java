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

import com.google.code.geobeagle.database.GpxTableWriterGpxFiles;
import com.google.inject.Inject;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;

public class LocAlreadyLoadedChecker {
    private final GpxTableWriterGpxFiles gpxTableWriterGpxFiles;
    private final SimpleDateFormat simpleDateFormat;

    // For testing.
    public LocAlreadyLoadedChecker(GpxTableWriterGpxFiles gpxTableWriterGpxFiles, SimpleDateFormat dateFormat) {
        this.gpxTableWriterGpxFiles = gpxTableWriterGpxFiles;
        this.simpleDateFormat = dateFormat;
    }

    @Inject
    public LocAlreadyLoadedChecker(GpxTableWriterGpxFiles gpxTableWriterGpxFiles) {
        this.gpxTableWriterGpxFiles = gpxTableWriterGpxFiles;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    }

    boolean isAlreadyLoaded(String source) {
        int len = source.length();
        String extension = source.substring(Math.max(0, len - 4), len).toLowerCase();

        if (!extension.equalsIgnoreCase(".loc"))
            return false;

        File file = new File(source);
        long lastModified = file.lastModified();
        String sqlDate = simpleDateFormat.format(lastModified);
        Log.d("GeoBeagle", "GET NAME: " + sqlDate + ", " + source + ", " + lastModified);

        if (gpxTableWriterGpxFiles.isGpxAlreadyLoaded(file.getName(), sqlDate)) {
            return true;
        }
        return false;
    }

}
