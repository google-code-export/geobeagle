
package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.BCachingAnnotations.DetailsReaderAnnotation;
import com.google.code.geobeagle.bcaching.communication.BCachingException;
import com.google.code.geobeagle.bcaching.communication.BCachingListImportHelper.BufferedReaderFactory;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.code.geobeagle.xmlimport.CachePersisterFacade;
import com.google.code.geobeagle.xmlimport.CacheTagSqlWriter;
import com.google.code.geobeagle.xmlimport.EventHandlerGpx;
import com.google.code.geobeagle.xmlimport.EventHelper;
import com.google.code.geobeagle.xmlimport.FileAlreadyLoadedChecker;
import com.google.code.geobeagle.xmlimport.GpxLoader;
import com.google.code.geobeagle.xmlimport.GpxToCache;
import com.google.code.geobeagle.xmlimport.MessageHandlerInterface;
import com.google.code.geobeagle.xmlimport.CachePersisterFacadeDI.FileFactory;
import com.google.code.geobeagle.xmlimport.EventHelper.XmlPathBuilder;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.ProgressDialogWrapper;
import com.google.code.geobeagle.xmlimport.GpxToCache.Aborter;
import com.google.code.geobeagle.xmlimport.GpxToCacheDI.XmlPullParserWrapper;
import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import java.io.BufferedReader;
import java.util.Hashtable;

public class DetailsReaderImport {
    static class MessageHandlerAdapter implements MessageHandlerInterface {

        private ProgressManager progressManager;
        private final Handler handler;

        public MessageHandlerAdapter(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void abortLoad() {

        }

        @Override
        public void deletingCacheFiles() {

        }

        @Override
        public void handleMessage(Message msg) {
        }

        @Override
        public void loadComplete() {
        }

        @Override
        public void start(CacheListRefresh cacheListRefresh) {
        }

        @Override
        public void updateName(String name) {
            Log.d("GeoBeagle", handler + ", " + progressManager + ": UPDATING NAME " + name);
            progressManager.update(handler, ProgressMessage.SET_FILE, name);
        }

        @Override
        public void updateSource(String text) {
        }

        @Override
        public void updateStatus(String status) {
        }

        @Override
        public void updateWaypointId(String wpt) {
        }

        public void setProgressManager(ProgressManager progressManager) {
            this.progressManager = progressManager;
        }

    }

    static interface DetailsReaderImportFactory {
        DetailsReaderImport create(Handler handler);
    }

    private final Hashtable<String, String> params;
    private final BufferedReaderFactory bufferedReaderFactory;
    private final GpxLoader gpxLoader;
    private EventHelper eventHelper;
    private ProgressDialogWrapper progressDialogWrapper;
    private CachePersisterFacade cachePersisterFacade;

    @Inject
    DetailsReaderImport(@DetailsReaderAnnotation Hashtable<String, String> params,
            BufferedReaderFactory bufferedReaderFactory, FileFactory fileFactory,
            CacheDetailsWriter cacheDetailsWriter, ErrorDisplayer errorDisplayer,
            WakeLock wakeLock, XmlPullParserWrapper xmlPullParserWrapper,
            XmlPathBuilder xmlPathBuilder, Aborter aborter, CacheTagSqlWriter cacheTagSqlWriter,
            FileAlreadyLoadedChecker fileAlreadyLoadedChecker) {
        this.params = params;
        this.bufferedReaderFactory = bufferedReaderFactory;
        cachePersisterFacade = new CachePersisterFacade(cacheTagSqlWriter, fileFactory,
                cacheDetailsWriter, null, wakeLock);
        GpxToCache gpxToCache = new GpxToCache(xmlPullParserWrapper, aborter,
                fileAlreadyLoadedChecker);
        this.gpxLoader = new GpxLoader(cachePersisterFacade, errorDisplayer, gpxToCache, wakeLock);

        EventHandlerGpx eventHandlerGpx = new EventHandlerGpx(cachePersisterFacade);
        eventHelper = new EventHelper(xmlPathBuilder, eventHandlerGpx, xmlPullParserWrapper);
    }

    public void getCacheDetails(String csvIds, ProgressManager progressManager, Handler handler,
            int updatedCaches) throws BCachingException {
        MessageHandlerAdapter messageHandlerAdapter = new MessageHandlerAdapter(handler);

        messageHandlerAdapter.setProgressManager(progressManager);
        cachePersisterFacade.setHandler(messageHandlerAdapter);
        params.put("ids", csvIds);
        messageHandlerAdapter.updateName("getCacheDetails");

        try {
            BufferedReader bufferedReader = bufferedReaderFactory.create(params);
            gpxLoader.open("BCaching.com." + updatedCaches, bufferedReader);
        } catch (XmlPullParserException e) {
            throw new BCachingException("Error parsing data from baching.com: "
                    + e.getLocalizedMessage());
        }
        gpxLoader.load(eventHelper);
    }
}
