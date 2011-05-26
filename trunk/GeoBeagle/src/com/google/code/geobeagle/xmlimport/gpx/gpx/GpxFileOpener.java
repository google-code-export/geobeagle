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

import com.google.code.geobeagle.xmlimport.AbortState;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReader;
import com.google.code.geobeagle.xmlimport.gpx.IGpxReaderIter;
import com.google.inject.Provider;

public class GpxFileOpener {
    public static class GpxFileIter implements IGpxReaderIter {

        private final Provider<AbortState> aborterProvider;
        private String filename;

        public GpxFileIter(Provider<AbortState> aborterProvider, String filename) {
            this.aborterProvider = aborterProvider;
            this.filename = filename;
        }

        @Override
        public boolean hasNext() {
            if (aborterProvider.get().isAborted())
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

    private final Provider<AbortState> aborterProvider;
    private final String filename;

    public GpxFileOpener(String filename, Provider<AbortState> aborterProvider) {
        this.filename = filename;
        this.aborterProvider = aborterProvider;
    }

    public GpxFileIter iterator() {
        return new GpxFileIter(aborterProvider, filename);
    }
}
