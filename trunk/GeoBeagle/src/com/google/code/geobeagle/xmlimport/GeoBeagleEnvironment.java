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

package com.google.code.geobeagle.xmlimport;

import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.os.Environment;

public class GeoBeagleEnvironment {
    public static final String IMPORT_FOLDER = "import-folder";
    private final SharedPreferences sharedPreferences;
    private static final String DETAILS_DIR = "GeoBeagle/data/";
    private static final String FIELDNOTES_FILE = "GeoBeagleFieldNotes.txt";

    @Inject
    GeoBeagleEnvironment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getExternalStorageDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getDetailsDirectory() {
        return getExternalStorageDir() + "/" + GeoBeagleEnvironment.DETAILS_DIR;
    }

    public String getVersionPath() {
        return getDetailsDirectory() + "/VERSION";
    }

    public String getOldDetailsDirectory() {
        return getExternalStorageDir()  + "/" + "GeoBeagle";
    }

    public String getImportFolder() {
        String string = sharedPreferences.getString(IMPORT_FOLDER, Environment
                .getExternalStorageDirectory()
                + "/Download");
        if ((!string.endsWith("/")))
            return string + "/";
        return string;
    }

    public String getFieldNotesFilename() {
        return getExternalStorageDir() + "/" + FIELDNOTES_FILE;
    }
}
