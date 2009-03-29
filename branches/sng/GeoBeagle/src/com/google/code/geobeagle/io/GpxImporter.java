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
import com.google.code.geobeagle.io.DatabaseDI.SQLiteWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.GpxFilenameFactory;
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.app.ListActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

public class GpxImporter {

    public static FilenameFilter filenameFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return !name.startsWith(".") && name.endsWith(".gpx");
        }
    };

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
                if (children.length == 0) {
                    mErrorDisplayer.displayError(R.string.error_no_gpx_files);
                    return;
                }
                mGpxLoader.start();
                for (String child : children) {
                    filename = "/sdcard/" + child;
                    mGpxLoader.open(filename);
                    if (!mGpxLoader.load())
                        return;
                }
                mGpxLoader.end();
            } catch (final FileNotFoundException e) {
                mErrorDisplayer.displayError(R.string.error_opening_file, filename);
            } catch (Exception e) {
                mErrorDisplayer.displayErrorAndStack(e);
            } finally {
                mMessageHandler.loadComplete();
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

    GpxImporter(GpxLoader gpxLoader, Database database, SQLiteWrapper sqliteWrapper,
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
