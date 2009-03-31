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
import com.google.code.geobeagle.io.GpxImporterDI.ImportThreadWrapper;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.io.GpxImporterDI.ToastFactory;
import com.google.code.geobeagle.ui.CacheListDelegate;
import com.google.code.geobeagle.ui.ErrorDisplayer;


import android.app.ListActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.Iterator;

public class GpxImporter {
    static class GpxFile {
        private final String mFilename;

        public GpxFile(String filename) {
            mFilename = filename;
        }

        public String getFilename() {
            return mFilename;
        }

        public Reader open() throws FileNotFoundException {
            return new FileReader("/sdcard/" + mFilename);
        }
    }

    static class GpxFilenameFactory {
        private final FilenameFilter mFilenameFilter;

        public GpxFilenameFactory(FilenameFilter filenameFilter) {
            mFilenameFilter = filenameFilter;
        }

        public String[] getFilenames() {
            return new File("/sdcard").list(mFilenameFilter);
        }
    }

    static class GpxFiles implements Iterable<GpxFile> {
        public class GpxFileIterator implements Iterator<GpxFile> {
            private final String[] mFileList;
            private int mIx;

            public GpxFileIterator(String[] fileList) {
                mFileList = fileList;
                mIx = 0;
            }

            public boolean hasNext() {
                return mIx < mFileList.length;
            }

            public GpxFile next() {
                return new GpxFile(mFileList[mIx++]);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        private FilenameFilter mFilenameFilter;

        public GpxFiles(FilenameFilter filenameFilter) {
            mFilenameFilter = filenameFilter;
        }

        public Iterator<GpxFile> iterator() {
            String[] fileList = new File("/sdcard").list(mFilenameFilter);
            return new GpxFileIterator(fileList);
        }
    }

    static public FilenameFilter filenameFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return !name.startsWith(".") && (name.endsWith(".gpx"));
            // || name.endsWith(".zip"));
        }
    };

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
