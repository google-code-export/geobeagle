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
import com.google.code.geobeagle.io.GpxImporter.GpxFile;
import com.google.code.geobeagle.io.GpxImporter.GpxFiles;
import com.google.code.geobeagle.io.GpxImporterDI.MessageHandler;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import java.io.FileNotFoundException;
import java.util.Iterator;

public class ImportThreadDelegate {
    public static class ImportThreadHelper {
        private final ErrorDisplayer mErrorDisplayer;
        private final GpxLoader mGpxLoader;
        private final MessageHandler mMessageHandler;

        public ImportThreadHelper(GpxLoader gpxLoader, MessageHandler messageHandler,
                ErrorDisplayer errorDisplayer) {
            mErrorDisplayer = errorDisplayer;
            mGpxLoader = gpxLoader;
            mMessageHandler = messageHandler;
        }

        public void cleanup() {
            mMessageHandler.loadComplete();
        }

        public void end() {
            mGpxLoader.end();
        }

        public boolean processFile(GpxFile gpxFile) {
            String filename = gpxFile.getFilename();
            try {
                mGpxLoader.open(filename, gpxFile.open());
                if (!mGpxLoader.load())
                    return false;
                return true;
            } catch (final FileNotFoundException e) {
                mErrorDisplayer.displayError(R.string.error_opening_file, filename);
            } catch (Exception e) {
                mErrorDisplayer.displayErrorAndStack(e);
            }
            return false;
        }

        public boolean start(boolean hasNext) {
            if (!hasNext) {
                mErrorDisplayer.displayError(R.string.error_no_gpx_files);
                return false;
            }

            mGpxLoader.start();
            return true;
        }
    }

    private final GpxFiles mGpxFiles;
    private ImportThreadHelper mImportThreadHelper;

    public ImportThreadDelegate(GpxFiles gpxFiles, ImportThreadHelper importThreadHelper) {
        mGpxFiles = gpxFiles;
        mImportThreadHelper = importThreadHelper;
    }

    public void run() {
        try {
            Iterator<GpxFile> gpxFileIter = mGpxFiles.iterator();
            if (!mImportThreadHelper.start(gpxFileIter.hasNext()))
                return;

            while (gpxFileIter.hasNext()) {
                if (!mImportThreadHelper.processFile(gpxFileIter.next()))
                    return;
            }
            mImportThreadHelper.end();
        } finally {
            mImportThreadHelper.cleanup();
        }
    }
}
