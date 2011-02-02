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

import roboguice.inject.ContextScoped;

import android.util.Log;

@ContextScoped
public class AbortState {
    private static boolean mAborted = false;

    AbortState() {
        mAborted = false;
    }

    public void abort() {
        Log.d("GeoBeagle", this + ": aborting");
        mAborted = true;
    }

    public boolean isAborted() {
        return mAborted;
    }

    public void reset() {
        mAborted = false;
    }
}
