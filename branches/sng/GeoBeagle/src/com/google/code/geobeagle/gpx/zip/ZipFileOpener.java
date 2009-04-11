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

package com.google.code.geobeagle.gpx.zip;

import com.google.code.geobeagle.gpx.IGpxReader;
import com.google.code.geobeagle.gpx.IGpxReaderIter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;

public class ZipFileOpener {
    public static class ZipFileIter implements IGpxReaderIter {
        private ZipEntry mNextZipEntry;
        private final GpxZipInputStream mZipInputStream;

        ZipFileIter(GpxZipInputStream zipInputStream) {
            mZipInputStream = zipInputStream;
            mNextZipEntry = null;
        }

        public boolean hasNext() throws IOException {
            if (mNextZipEntry == null)
                mNextZipEntry = mZipInputStream.getNextEntry();
            return mNextZipEntry != null;
        }

        public IGpxReader next() throws IOException {
            if (mNextZipEntry == null)
                mNextZipEntry = mZipInputStream.getNextEntry();

            if (mNextZipEntry == null)
                return null;

            final String name = mNextZipEntry.getName();
            mNextZipEntry = null;
            return new GpxReader(name, new InputStreamReader(mZipInputStream.getStream()));
        }
    }

    private final String mFilename;
    private final ZipInputStreamFactory mZipInputStreamFactory;

    public ZipFileOpener(String filename, ZipInputStreamFactory zipInputStreamFactory) {
        mFilename = filename;
        mZipInputStreamFactory = zipInputStreamFactory;
    }

    public ZipFileIter iterator() throws IOException {
        return new ZipFileIter(mZipInputStreamFactory.create(mFilename));
    }
}
