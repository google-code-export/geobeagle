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

package com.google.code.geobeagle.cachedetails;

import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileDataVersionChecker {
    private GeoBeagleEnvironment geoBeagleEnvironment;

    @Inject
    FileDataVersionChecker(GeoBeagleEnvironment geoBeagleEnvironment) {
        this.geoBeagleEnvironment = geoBeagleEnvironment;
    }

    public boolean needsUpdating() {
        if (true)
            return false;
        File file = new File(geoBeagleEnvironment.getVersionPath());
        if (!file.exists())
            return true;
        try {
            String line = new BufferedReader(new FileReader(file)).readLine();
            Log.d("GeoBeagle", "VERSION " + line);
            return Integer.valueOf(line) != 1;
        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException e) {
            return true;
        } catch (NumberFormatException e) {
            Log.e("GeoBeagle", "FileDateVersionChecker: " + e.getLocalizedMessage());
            return true;
        }
    }
}
