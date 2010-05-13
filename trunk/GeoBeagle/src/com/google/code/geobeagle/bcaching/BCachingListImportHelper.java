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
import com.google.code.geobeagle.bcaching.json.BCachingJSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Hashtable;

class BCachingListImportHelper {
    private final BCachingCommunication bcachingCommunication;

    public BCachingListImportHelper(BCachingCommunication bcachingCommunication) {
        this.bcachingCommunication = bcachingCommunication;
    }

    BCachingList importList(Hashtable<String, String> params) throws BCachingException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(bcachingCommunication
                .sendRequest(params), Charset.forName("UTF-8")), 8192);

        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
            String string = result.toString();
            Log.d("GeoBeagle", "readResponse: " + string);
            return new BCachingList(new BCachingJSONObject(new JSONObject(string)));
        } catch (IOException e) {
            throw new BCachingException("IO Error: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            throw new BCachingException("Error parsing data from server: "
                    + e.getLocalizedMessage());
        }
    }

}