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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.preferences.Preferences;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.code.geobeagle.xmlimport.ImportException;
import com.google.inject.Inject;

import android.content.SharedPreferences;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class GpxAndZipFiles {
    public static class GpxAndZipFilenameFilter implements FilenameFilter {
        private final GpxFilenameFilter mGpxFilenameFilter;

        @Inject
        public GpxAndZipFilenameFilter(GpxFilenameFilter gpxFilenameFilter) {
            mGpxFilenameFilter = gpxFilenameFilter;
        }

        @Override
        public boolean accept(File dir, String name) {
            String lowerCaseName = name.toLowerCase();
            if (!lowerCaseName.startsWith(".") && lowerCaseName.endsWith(".zip"))
                return true;
            return mGpxFilenameFilter.accept(lowerCaseName);
        }
    }

    public static class GpxFilenameFilter {
        public boolean accept(String name) {
            String lowerCaseName = name.toLowerCase();
            return !lowerCaseName.startsWith(".")
                    && (lowerCaseName.endsWith(".gpx") || lowerCaseName.endsWith(".loc"));
        }
    }

    public static class GpxFilesAndZipFilesIter {
        private final String[] mFileList;
        private final GpxFileIterAndZipFileIterFactory mGpxAndZipFileIterFactory;
        private int mIxFileList;
        private IGpxReaderIter mSubIter;

        GpxFilesAndZipFilesIter(String[] fileList,
                GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory) {
            mFileList = fileList;
            mGpxAndZipFileIterFactory = gpxFileIterAndZipFileIterFactory;
            mIxFileList = 0;
        }

        public boolean hasNext() throws IOException {
            // Iterate through actual zip, loc, and gpx files on the filesystem.
            // If a zip file, a sub iterator will walk through the zip file
            // contents, otherwise the sub iterator will return just the loc/gpx
            // file.
            if (mSubIter != null && mSubIter.hasNext())
                return true;

            while (mIxFileList < mFileList.length) {
                mSubIter = mGpxAndZipFileIterFactory.fromFile(mFileList[mIxFileList++]);
                if (mSubIter.hasNext())
                    return true;
            }
            return false;
        }

        public IGpxReader next() throws IOException {
            return mSubIter.next();
        }
    }

    private final FilenameFilter mFilenameFilter;
    private final GpxFileIterAndZipFileIterFactory mGpxFileIterAndZipFileIterFactory;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;
    private final SharedPreferences mSharedPreferences;

    @Inject
    public GpxAndZipFiles(GpxAndZipFilenameFilter filenameFilter,
            GpxFileIterAndZipFileIterFactory gpxFileIterAndZipFileIterFactory,
            GeoBeagleEnvironment geoBeagleEnvironment,
            SharedPreferences sharedPreferences) {
        mFilenameFilter = filenameFilter;
        mGpxFileIterAndZipFileIterFactory = gpxFileIterAndZipFileIterFactory;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
        mSharedPreferences = sharedPreferences;
    }

    public GpxFilesAndZipFilesIter iterator() throws ImportException {
        String[] fileList;
        if (!mSharedPreferences.getBoolean(Preferences.SDCARD_ENABLED, true)) {
            fileList = new String[0];
        } else {
            String gpxDir = mGeoBeagleEnvironment.getImportFolder();
            fileList = new File(gpxDir).list(mFilenameFilter);
            if (fileList == null)
                throw new ImportException(R.string.error_cant_read_sd, gpxDir);
        }
        mGpxFileIterAndZipFileIterFactory.resetAborter();
        return new GpxFilesAndZipFilesIter(fileList, mGpxFileIterAndZipFileIterFactory);
    }
}
