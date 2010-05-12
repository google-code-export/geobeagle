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

import com.google.code.geobeagle.bcaching.BCachingCommunication.BCachingException;
import com.google.code.geobeagle.cachedetails.WriterWrapper;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;

class DetailsReader {
    private final BCachingCommunication bcachingCommunication;

    DetailsReader(BCachingCommunication bcachingCommunication) {
        this.bcachingCommunication = bcachingCommunication;
    }

    void getCacheDetails(String csvIds, int updatedCaches) throws BCachingException {
        Log.d("GeoBeagle", "Getting details: " + updatedCaches);
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("a", "detail");
        params.put("desc", "html");
        params.put("ids", csvIds);
        params.put("tbs", "0");
        params.put("wpts", "1");
        params.put("logs", "1");
        params.put("fmt", "gpx");
        params.put("app", "GeoBeagle");

        Log.d("GeoBeagle", "Downloading cache details");
        DataInputStream dis = new DataInputStream(bcachingCommunication.sendRequest(params));
        WriterWrapper writerWrapper = new WriterWrapper();
        try {
            writerWrapper.open("/sdcard/download/bcaching" + String.valueOf(updatedCaches)
                    + ".gpx");
            String line;
            while ((line = dis.readLine()) != null) {
                writerWrapper.write(line);
            }
            writerWrapper.close();
        } catch (IOException e) {
            throw new BCachingException("IO Error: " + e.getLocalizedMessage());
        }
    }

}