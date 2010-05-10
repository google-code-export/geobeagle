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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
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
import java.util.Hashtable;

public class ImportBCachingWorker extends Thread {

    private final Handler handler;
    private final SharedPreferences sharedPreferences;

    public ImportBCachingWorker(Handler handler, SharedPreferences sharedPreferences) {
        this.handler = handler;
        this.sharedPreferences = sharedPreferences;
    }

    private JSONObject readResponse(InputStream in) throws IOException, JSONException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset
                .forName("UTF-8")), 8192);

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
            result.append('\n');
        }
        return new JSONObject(result.toString());
    }

    private void getCacheDetails(BcachingCommunication bcachingCommunication, String csvIds,
            int updatedCaches) throws MalformedURLException, Exception  {
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
        InputStream is = bcachingCommunication.sendRequest(params);
        DataInputStream dis = new DataInputStream(is);
        String line = null;
        WriterWrapper writerWrapper = new WriterWrapper();
        writerWrapper
                .open("/sdcard/download/bcaching" + String.valueOf(updatedCaches) + ".gpx");
        while ((line = dis.readLine()) != null) {
            writerWrapper.write(line);
        }
        writerWrapper.close();
    }

    @Override
    public void run() {
        String now = Long.toString(System.currentTimeMillis());
        try {
            String lastUpdate = sharedPreferences.getString("bcaching_lastupdate", "");
            BcachingCommunication bcachingCommunication = new BcachingCommunication("USERNAME",
                    "PASSWORD");
            Message.obtain(handler, ProgressMessage.START.ordinal()).sendToTarget();

            Hashtable<String, String> params = new Hashtable<String, String>();
            if (!lastUpdate.equals(""))
                params.put("since", lastUpdate);
            params.put("a", "list");
            params.put("maxcount", "50");
            params.put("found", "0");
            params.put("app", "GeoBeagle");

            int updatedCaches = 0;

            JSONObject obj = readResponse(bcachingCommunication.sendRequest(params));
            int totalCount = obj.getInt("totalCount");
            Message.obtain(handler, ProgressMessage.SET_MAX.ordinal(), totalCount, 0)
                    .sendToTarget();
            Log.d("GeoBeagle", "totalCount = " + totalCount);

            while (updatedCaches < totalCount) {
                params.put("first", Integer.toString(updatedCaches));

                if (updatedCaches > 0)
                    obj = readResponse(bcachingCommunication.sendRequest(params));

                JSONArray summary = obj.getJSONArray("data");
                Log.d("GeoBeagle", summary.toString());

                int count = summary.length();
                StringBuilder csvIds = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    JSONObject cacheObject = summary.getJSONObject(i);
                    int id = cacheObject.getInt("id");
                    if (csvIds.length() > 0) {
                        csvIds.append(',');
                    }
                    csvIds.append(String.valueOf(id));
                }

                updatedCaches += count;
                Message.obtain(handler, ProgressMessage.SET_PROGRESS.ordinal(), updatedCaches, 0)
                        .sendToTarget();
                getCacheDetails(bcachingCommunication, csvIds.toString(), updatedCaches);
            }
        } catch (Exception ex) {

        }
        Message.obtain(handler, ProgressMessage.DONE.ordinal(), 0).sendToTarget();
        Log.d("GeoBeagle", "Setting bcaching_lastupdate to " + now);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("bcaching_lastupdate", now);
        editor.commit();
    }
}