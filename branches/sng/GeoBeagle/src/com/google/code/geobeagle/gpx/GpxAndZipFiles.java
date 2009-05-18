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


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class GpxAndZipFiles {
    public static class GpxAndZipFilenameFilter implements FilenameFilter {
        private final GpxFilenameFilter mGpxFilenameFilter;

        public GpxAndZipFilenameFilter(GpxFilenameFilter gpxFilenameFilter) {
            mGpxFilenameFilter = gpxFilenameFilter;
        }

        public boolean accept(File dir, String name) {
            name = name.toLowerCase();
            if (!name.startsWith(".") && name.endsWith(".zip"))
                return true;
            return mGpxFilenameFilter.accept(name);
        }
    }

    public static class GpxFilenameFilter {
        public boolean accept(String name) {
            name = name.toLowerCase();
            return !name.startsWith(".") && (name.endsWith(".gpx") || name.endsWith(".loc"));
        }
    }

    public static class GpxAndZipFilesIter {
        private final String[] mFileList;
        private final GpxAndZipFilesDI.GpxAndZipFilesIterFactory mGpxAndZipFileIterFactory;
        private int mIxFileList;
        private IGpxReaderIter mSubIter;

        GpxAndZipFilesIter(String[] fileList, GpxAndZipFilesDI.GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory) {
            mFileList = fileList;
            mGpxAndZipFileIterFactory = gpxAndZipFilesIterFactory;
            mIxFileList = 0;
        }

        public boolean hasNext() throws IOException {
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

    public static final String GPX_DIR = "/sdcard/download/";
    private final FilenameFilter mFilenameFilter;
    private final GpxAndZipFilesDI.GpxAndZipFilesIterFactory mGpxFileIterFactory;

    public GpxAndZipFiles(FilenameFilter filenameFilter,
            GpxAndZipFilesDI.GpxAndZipFilesIterFactory gpxFileIterFactory) {
        mFilenameFilter = filenameFilter;
        mGpxFileIterFactory = gpxFileIterFactory;
    }

    public GpxAndZipFilesIter iterator() {
        String[] fileList = new File(GPX_DIR).list(mFilenameFilter);
        if (fileList == null)
            return null;
        return new GpxAndZipFilesIter(fileList, mGpxFileIterFactory);
    }
}
