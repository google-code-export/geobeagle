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

package com.google.code.geobeagle.xmlimport.gpx.gpx;

import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReaderIter;

public class GpxFileOpener {
    public static class GpxFileIter implements IGpxReaderIter {

        private final Aborter aborter;
        private String filename;

        public GpxFileIter(Aborter aborter, String filename) {
            this.aborter = aborter;
            this.filename = filename;
        }

        @Override
        public boolean hasNext() {
            if (aborter.isAborted())
                return false;
            return filename != null;
        }

        @Override
        public IGpxReader next() {
            final IGpxReader gpxReader = new GpxReader(filename);
            filename = null;
            return gpxReader;
        }
    }

    private final Aborter mAborter;
    private final String mFilename;

    public GpxFileOpener(String filename, Aborter aborter) {
        mFilename = filename;
        mAborter = aborter;
    }

    public GpxFileIter iterator() {
        return new GpxFileIter(mAborter, mFilename);
    }
}
