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

package com.google.code.geobeagle.gpx;

import com.google.code.geobeagle.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;

import java.io.IOException;

public class GpxAndZipFilesDI {

    public static class GpxAndZipFilesIterFactory {
        GpxAndZipFiles.GpxFilenameFilter mGpxFilenameFilter;

        public GpxAndZipFilesIterFactory(GpxAndZipFiles.GpxFilenameFilter gpxFilenameFilter) {
            mGpxFilenameFilter = gpxFilenameFilter;
        }

        public IGpxReaderIter fromFile(String filename) throws IOException {
            if (filename.endsWith(".zip")) {
                return new ZipFileOpener(GpxAndZipFiles.GPX_DIR + filename,
                        new ZipInputStreamFactory(), mGpxFilenameFilter).iterator();
            }
            return new GpxFileOpener(filename).iterator();
        }
    }

}
