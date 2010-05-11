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

import com.google.code.geobeagle.cachedetails.WriterWrapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

public class ImportBCachingWorker extends Thread {
    public static interface ImportBCachingWorkerFactory {
        ImportBCachingWorker create(Handler handler);
    }

    private final Handler handler;
    private final BCachingLastUpdated bcachingLastUpdated;
    private final BCachingListFactory bCachingListFactory;
    private final BCachingCommunication bCachingCommunication;

    @Inject
    public ImportBCachingWorker(@Assisted Handler handler, BCachingLastUpdated bcachingLastUpdated,
            BCachingListFactory bCachingListFactory, BCachingCommunication bCachingCommunication) {
        this.handler = handler;
        this.bcachingLastUpdated = bcachingLastUpdated;
        this.bCachingListFactory = bCachingListFactory;
        this.bCachingCommunication = bCachingCommunication;
    }

    static JSONObject readResponse(InputStream in) throws IOException, JSONException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")),
                8192);

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
            result.append('\n');
        }
        String string = result.toString();
        Log.d("GeoBeagle", "readResponse: " + string);
        return new JSONObject(string);
    }

    private void getCacheDetails(BCachingCommunication bcachingCommunication, String csvIds,
            int updatedCaches) throws MalformedURLException, Exception {
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
        writerWrapper.open("/sdcard/download/bcaching" + String.valueOf(updatedCaches) + ".gpx");
        String line;
        while ((line = dis.readLine()) != null) {
            writerWrapper.write(line);
        }
        writerWrapper.close();
    }

    static class BCachingList {
        private final JSONObject cacheList;

        BCachingList(JSONObject json) {
            this.cacheList = json;
        }

        int getCount() throws JSONException {
            return cacheList.getJSONArray("data").length();
        }

        int getTotalCount() throws JSONException {
            return cacheList.getInt("totalCount");
        }

        String getCacheIds() throws JSONException {
            JSONArray summary = cacheList.getJSONArray("data");
            Log.d("GeoBeagle", summary.toString());

            StringBuilder csvIds = new StringBuilder();
            int count = summary.length();
            for (int i = 0; i < count; i++) {
                JSONObject cacheObject = summary.getJSONObject(i);
                int id = cacheObject.getInt("id");
                if (csvIds.length() > 0) {
                    csvIds.append(',');
                }
                csvIds.append(String.valueOf(id));
            }
            return csvIds.toString();
        }
    }

    static class BCachingListFactory {
        private final Hashtable<String, String> params;
        private final BCachingCommunication bCachingCommunication;
        private BCachingList firstCacheList;

        @Inject
        BCachingListFactory(Hashtable<String, String> params,
                BCachingCommunication bCachingCommunication) {
            this.params = params;
            this.bCachingCommunication = bCachingCommunication;
        }

        int getTotalCount() throws MalformedURLException, IOException, JSONException,
                NoSuchAlgorithmException, DigestException, HttpException {
            params.remove("first");
            firstCacheList = new BCachingList(readResponse(bCachingCommunication
                    .sendRequest(params)));
            return firstCacheList.getTotalCount();
        }

        void putLastUpdateTime(String lastUpdate) {
            if (!lastUpdate.equals(""))
                params.put("since", lastUpdate);
        }

        BCachingList getCacheList(int startAt) throws NoSuchAlgorithmException, DigestException,
                IOException, JSONException, HttpException {
            if (startAt == 0)
                return firstCacheList;

            params.put("first", Integer.toString(startAt));
            return new BCachingList(readResponse(bCachingCommunication.sendRequest(params)));
        }

    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        try {
            Message.obtain(handler, ProgressMessage.START.ordinal()).sendToTarget();
            bCachingListFactory.putLastUpdateTime(bcachingLastUpdated.getLastUpdateTime());

            int totalCount = bCachingListFactory.getTotalCount();
            Message.obtain(handler, ProgressMessage.SET_MAX.ordinal(), totalCount, 0)
                    .sendToTarget();
            Log.d("GeoBeagle", "totalCount = " + totalCount);

            int updatedCaches = 0;
            while (updatedCaches < totalCount) {
                BCachingList bcachingList = bCachingListFactory.getCacheList(updatedCaches);

                updatedCaches += bcachingList.getCount();
                Log.d("GeoBeagle", "updated caches: " + updatedCaches);
                Message.obtain(handler, ProgressMessage.SET_PROGRESS.ordinal(), updatedCaches, 0)
                        .sendToTarget();
                getCacheDetails(bCachingCommunication, bcachingList.getCacheIds(), updatedCaches);
                if (updatedCaches < 50)
                    break;
            }
        } catch (Exception ex) {
            Log.d("GeoBeagle", "Exception: " + ex);
        }

        Message.obtain(handler, ProgressMessage.DONE.ordinal(), 0).sendToTarget();
        Log.d("GeoBeagle", "Setting bcaching_lastupdate to " + now);
        bcachingLastUpdated.putLastUpdateTime(now);
    }

}
