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

import com.google.code.geobeagle.bcaching.BCachingAnnotations.CacheListAnnotation;
import com.google.code.geobeagle.bcaching.BCachingAnnotations.DetailsReaderAnnotation;
import com.google.code.geobeagle.bcaching.communication.BCachingCommunication;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImportHelper.BufferedReaderFactory;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.database.ClearCachesFromSource;
import com.google.code.geobeagle.database.ClearCachesFromSourceNull;
import com.google.code.geobeagle.xmlimport.CachePersisterFacade;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.MessageHandlerInterface;
import com.google.code.geobeagle.xmlimport.XmlPullParserWrapper;
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.XmlimportAnnotations.LoadDetails;
import com.google.inject.Inject;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.util.RoboThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class BCachingModule extends AbstractAndroidModule {

    public static final String BCACHING_USERNAME = "bcaching-username";
    public static final String BCACHING_PASSWORD = "bcaching-password";
    public static final String BCACHING_INITIAL_MESSAGE = "Getting cache count...";

    static class ImportSubmodule extends AbstractAndroidModule {

        @Override
        protected void configure() {
        }

        @Provides
        @LoadDetails
        @ContextScoped
        EventHelper eventHelperGpxLoadDetailsProvider(XmlPathBuilder xmlPathBuilder,
                @LoadDetails EventHandlerGpx eventHandlerGpx, XmlPullParserWrapper xmlPullParser) {
            return new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParser);
        }
        
        @Provides
        WakeLock wakeLockProvider(PowerManager powerManager) {
            return powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
        }
 
    }
    
    @Override
    protected void configure() {
        bind(BufferedReaderFactory.class).to(BufferedReaderFactoryImpl.class);
        bind(ProgressHandler.class).in(ContextScoped.class);
        bind(MessageHandlerInterface.class).to(MessageHandlerAdapter.class);
        bind(CachePersisterFacade.class).in(ContextScoped.class);
        bind(Aborter.class).in(ContextScoped.class);
        bind(ClearCachesFromSource.class).to(ClearCachesFromSourceNull.class);
        bind(ImportBCachingWorker.class).in(ContextScoped.class);
        install(new ImportSubmodule());
        requestStaticInjection(RoboThread.class);
    }

    @ContextScoped
    @Provides
    ProgressDialog progressDialogProvider(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Sync from BCaching.com");
        progressDialog.setMessage(BCACHING_INITIAL_MESSAGE);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    static class BufferedReaderFactoryImpl implements BufferedReaderFactory {
        private final BCachingCommunication bcachingCommunication;

        @Inject
        public BufferedReaderFactoryImpl(BCachingCommunication bcachingCommunication) {
            this.bcachingCommunication = bcachingCommunication;
        }

        public BufferedReader create(Hashtable<String, String> params) throws BCachingException {
            InputStream inputStream = bcachingCommunication.sendRequest(params);
            return new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")),
                    8192);
        }
    }

    @Provides
    @DetailsReaderAnnotation
    Hashtable<String, String> getCacheDetailsParamsProvider() {
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("a", "detail");
        commonParams(params);
        params.put("desc", "html");
        params.put("tbs", "0");
        params.put("wpts", "1");
        params.put("logs", "1");
        params.put("fmt", "gpx");
        return params;
    }

    private void commonParams(Hashtable<String, String> params) {
        params.put("lastuploaddays", "7");
        params.put("app", "GeoBeagle");
        params.put("timeAsLong", "1");
    }

    @Provides
    @CacheListAnnotation
    Hashtable<String, String> getCacheListParamsProvider() {
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("a", "list");
        params.put("found", "0");
        commonParams(params);
        return params;
    }
}
