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

package com.google.code.geobeagle.io;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.io.Database.SQLiteWrapper;
import com.google.code.geobeagle.io.di.GpxImporterDI.GpxFilenameFactory;
import com.google.code.geobeagle.io.di.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.di.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.di.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GpxImporter {
    public static class ImportThreadDelegate {
        private final ErrorDisplayer mErrorDisplayer;
        private final GpxFilenameFactory mGpxFilenameFactory;
        private final GpxLoader mGpxLoader;
        private final MessageHandler mMessageHandler;

        public ImportThreadDelegate(GpxLoader gpxLoader, MessageHandler messageHandler,
                ErrorDisplayer errorDisplayer, GpxFilenameFactory gpxFilenameFactory) {
            mMessageHandler = messageHandler;
            mGpxLoader = gpxLoader;
            mErrorDisplayer = errorDisplayer;
            mGpxFilenameFactory = gpxFilenameFactory;
        }

        public void run() {
            String filename = "";
            try {
                String children[] = mGpxFilenameFactory.getFilenames();
                mGpxLoader.start();
                for (String child : children) {
                    filename = "/sdcard/" + child;
                    mGpxLoader.open(filename);
                    mGpxLoader.load();
                }

                mMessageHandler.loadComplete();
            } catch (final FileNotFoundException e) {
                mErrorDisplayer.displayError(R.string.error_opening_file, filename);
            } catch (XmlPullParserException e) {
                mErrorDisplayer.displayError(R.string.error_parsing_file, filename);
            } catch (IOException e) {
                mErrorDisplayer.displayError(R.string.error_reading_file, filename);
            } catch (Exception e) {
                mErrorDisplayer.displayErrorAndStack(e);
            }
        }
    }

    private final Database mDatabase;
    private final ErrorDisplayer mErrorDisplayer;
    private final GpxLoader mGpxLoader;
    private final ImportThreadWrapper mImportThreadWrapper;
    private final ListActivity mListActivity;
    private final MessageHandler mMessageHandler;
    private final SQLiteWrapper mSqliteWrapper;
    private final ToastFactory mToastFactory;

    public GpxImporter(GpxLoader gpxLoader, Database database, SQLiteWrapper sqliteWrapper,
            ListActivity listActivity, ImportThreadWrapper importThreadWrapper,
            MessageHandler messageHandler, ErrorDisplayer errorDisplayer, ToastFactory toastFactory) {
        mSqliteWrapper = sqliteWrapper;
        mDatabase = database;
        mListActivity = listActivity;
        mGpxLoader = gpxLoader;
        mImportThreadWrapper = importThreadWrapper;
        mMessageHandler = messageHandler;
        mErrorDisplayer = errorDisplayer;
        mToastFactory = toastFactory;
    }

    public void abort() throws InterruptedException {
        mMessageHandler.abortLoad();
        mGpxLoader.abort();
        if (mImportThreadWrapper.isAlive()) {
            mImportThreadWrapper.join();
            mSqliteWrapper.close();
            mToastFactory.showToast(mListActivity, R.string.import_canceled, Toast.LENGTH_SHORT);
        }
    }

    public void importGpxs(CacheListDelegate cacheListDelegate) {
        mSqliteWrapper.openReadableDatabase(mDatabase);
        mImportThreadWrapper.open(cacheListDelegate, mGpxLoader, mErrorDisplayer);
        mImportThreadWrapper.start();
    }
}
