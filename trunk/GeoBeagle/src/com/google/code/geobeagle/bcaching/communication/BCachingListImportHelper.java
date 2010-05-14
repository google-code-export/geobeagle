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

package com.google.code.geobeagle.bcaching.communication;

import com.google.inject.Inject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

public class BCachingListImportHelper {
    public interface BufferedReaderFactory {
        BufferedReader create(Hashtable<String, String> params) throws BCachingException;
    }

    private final BCachingList.BCachingListFactory bcachingListFactory;
    private final BufferedReaderFactory bufferedReaderFactory;

    @Inject
    BCachingListImportHelper(BufferedReaderFactory readerFactory,
            BCachingList.BCachingListFactory bcachingListFactory) {
        this.bufferedReaderFactory = readerFactory;
        this.bcachingListFactory = bcachingListFactory;
    }

    BCachingList importList(Hashtable<String, String> params) throws BCachingException {
        BufferedReader bufferedReader = bufferedReaderFactory.create(params);
        StringBuilder result = new StringBuilder();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("GeoBeagle", "importList: " + line);
                result.append(line);
                result.append('\n');
            }
            return bcachingListFactory.create(result.toString());
        } catch (IOException e) {
            throw new BCachingException("IO Error: " + e.getLocalizedMessage());
        }
    }

}
