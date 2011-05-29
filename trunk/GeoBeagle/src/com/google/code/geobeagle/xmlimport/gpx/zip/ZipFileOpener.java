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

package com.google.code.geobeagle.xmlimport.gpx.zip;

import com.google.code.geobeagle.gpx.zip.ZipInputStreamFactory;
import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.code.geobeagle.xmlimport.gpx.GpxAndZipFiles.GpxFilenameFilter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReaderIter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;

public class ZipFileOpener {

    public static class ZipFileIter implements IGpxReaderIter {
        private final Provider<AbortState> mAborterProvider;
        private ZipEntry mNextZipEntry;
        private final ZipInputFileTester mZipInputFileTester;
        private final GpxZipInputStream mZipInputStream;

        ZipFileIter(GpxZipInputStream zipInputStream,
                Provider<AbortState> aborterProvider,
                ZipInputFileTester zipInputFileTester, ZipEntry nextZipEntry) {
            mZipInputStream = zipInputStream;
            mNextZipEntry = nextZipEntry;
            mAborterProvider = aborterProvider;
            mZipInputFileTester = zipInputFileTester;
        }

        ZipFileIter(GpxZipInputStream zipInputStream,
                Provider<AbortState> aborterProvider,
                ZipInputFileTester zipInputFileTester) {
            mZipInputStream = zipInputStream;
            mNextZipEntry = null;
            mAborterProvider = aborterProvider;
            mZipInputFileTester = zipInputFileTester;
        }

        @Override
        public boolean hasNext() throws IOException {
            // Iterate through zip file entries.
            if (mNextZipEntry == null) {
                do {
                    if (mAborterProvider.get().isAborted())
                        break;
                    mNextZipEntry = mZipInputStream.getNextEntry();
                } while (mNextZipEntry != null && !mZipInputFileTester.isValid(mNextZipEntry));
            }
            return mNextZipEntry != null;
        }

        @Override
        public IGpxReader next() throws IOException {
            final String name = mNextZipEntry.getName();
            mNextZipEntry = null;
            return new GpxReader(name, new InputStreamReader(mZipInputStream.getStream()));
        }
    }

    public static class ZipInputFileTester {
        private final GpxFilenameFilter mGpxFilenameFilter;

        @Inject
        public ZipInputFileTester(GpxFilenameFilter gpxFilenameFilter) {
            mGpxFilenameFilter = gpxFilenameFilter;
        }

        public boolean isValid(ZipEntry zipEntry) {
            return (!zipEntry.isDirectory() && mGpxFilenameFilter.accept(zipEntry.getName()));
        }
    }

    private final Provider<AbortState> mAborterProvider;
    private final String mFilename;
    private final ZipInputFileTester mZipInputFileTester;
    private final ZipInputStreamFactory mZipInputStreamFactory;

    public ZipFileOpener(String filename,
            ZipInputStreamFactory zipInputStreamFactory,
            ZipInputFileTester zipInputFileTester,
            Provider<AbortState> aborterProvider) {
        mFilename = filename;
        mZipInputStreamFactory = zipInputStreamFactory;
        mAborterProvider = aborterProvider;
        mZipInputFileTester = zipInputFileTester;
    }

    public ZipFileIter iterator() throws IOException {
        return new ZipFileIter(mZipInputStreamFactory.create(mFilename), mAborterProvider,
                mZipInputFileTester);
    }
}
