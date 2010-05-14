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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.bcaching.BCachingAnnotations.DetailsReaderAnnotation;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImportHelper.BufferedReaderFactory;
import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.inject.Inject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

public class DetailsReader {
    private final Hashtable<String, String> params;
    private final WriterWrapperFactory writerWrapperFactory;
    private final BufferedReaderFactory bufferedReaderFactory;

    interface WriterWrapperFactory {
        WriterWrapper create(String path) throws IOException;
    }

    @Inject
    DetailsReader(@DetailsReaderAnnotation Hashtable<String, String> params,
            BufferedReaderFactory bufferedReaderFactory, WriterWrapperFactory writerWrapperFactory) {
        this.params = params;
        this.bufferedReaderFactory = bufferedReaderFactory;
        this.writerWrapperFactory = writerWrapperFactory;
    }

    public void getCacheDetails(String csvIds, int updatedCaches) throws BCachingException {
        params.put("ids", csvIds);

        Log.d("GeoBeagle", "Downloading cache details");
        BufferedReader bufferedReader = bufferedReaderFactory.create(params);
        try {
            WriterWrapper writerWrapper = writerWrapperFactory.create("/sdcard/download/bcaching"
                    + String.valueOf(updatedCaches) + ".gpx");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                writerWrapper.write(line);
            }
            writerWrapper.close();
        } catch (IOException e) {
            throw new BCachingException("IO Error: " + e.getLocalizedMessage());
        }
    }
}
