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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class GpxAndZipFiles {
    public static class GpxAndZipFilesIter {
        private final String[] mFileList;
        private final GpxAndZipFilesIterFactory mGpxAndZipFileIterFactory;
        private int mIxFileList;
        private IGpxReaderIter mSubIter;

        GpxAndZipFilesIter(String[] fileList, GpxAndZipFilesIterFactory gpxAndZipFilesIterFactory) {
            mFileList = fileList;
            mGpxAndZipFileIterFactory = gpxAndZipFilesIterFactory;
            mIxFileList = 0;
        }

        public boolean hasNext() throws IOException {
            if (mSubIter != null && mSubIter.hasNext())
                return true;
            return mIxFileList < mFileList.length;
        }

        public IGpxReader next() throws IOException {
            if (mSubIter == null || !mSubIter.hasNext())
                mSubIter = mGpxAndZipFileIterFactory.fromFile(mFileList[mIxFileList++]);

            return mSubIter.next();
        }
    }

    public static class GpxAndZipFilesIterFactory {
        public IGpxReaderIter fromFile(String filename) throws IOException {
            if (filename.endsWith(".zip")) {
                return new ZipFileOpener(GPX_DIR + filename, new ZipInputStreamFactory())
                        .iterator();
            }
            return new GpxFileOpener(filename).iterator();
        }
    }

    public static class GpxAndZipFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            name = name.toLowerCase();
            return !name.startsWith(".")
                    && (name.endsWith(".gpx") || name.endsWith(".zip") || name.endsWith(".loc"));
        }
    }

    private final FilenameFilter mFilenameFilter;
    private final GpxAndZipFilesIterFactory mGpxFileIterFactory;
    public static final String GPX_DIR = "/sdcard/download/";

    public GpxAndZipFiles(FilenameFilter filenameFilter,
            GpxAndZipFilesIterFactory gpxFileIterFactory) {
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
