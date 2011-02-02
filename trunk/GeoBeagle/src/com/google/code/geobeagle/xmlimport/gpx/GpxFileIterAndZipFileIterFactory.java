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

package com.google.code.geobeagle.xmlimport.gpx;

import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.code.geobeagle.xmlimport.gpx.gpx.GpxFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener;
import com.google.code.geobeagle.xmlimport.gpx.zip.ZipFileOpener.ZipInputFileTester;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;

/**
 * @author sng
 *
 * Takes a filename and returns an IGpxReaderIter based on the
 * extension:
 *
 * zip: ZipFileIter
 * gpx/loc: GpxFileIter
 */
public class GpxFileIterAndZipFileIterFactory {
    private final Provider<AbortState> mAborterProvider;
    private final ZipInputFileTester mZipInputFileTester;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;

    @Inject
    public GpxFileIterAndZipFileIterFactory(ZipInputFileTester zipInputFileTester,
            Provider<AbortState> aborterProvider,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        mAborterProvider = aborterProvider;
        mZipInputFileTester = zipInputFileTester;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
    }

    public IGpxReaderIter fromFile(String filename) throws IOException {
        String importFolder = mGeoBeagleEnvironment.getImportFolder();
        if (filename.endsWith(".zip")) {
            return new ZipFileOpener(importFolder + filename, new ZipInputStreamFactory(),
                    mZipInputFileTester, mAborterProvider).iterator();
        }
        return new GpxFileOpener(importFolder + filename, mAborterProvider).iterator();
    }

    public void resetAborter() {
        mAborterProvider.get().reset();
    }
}
