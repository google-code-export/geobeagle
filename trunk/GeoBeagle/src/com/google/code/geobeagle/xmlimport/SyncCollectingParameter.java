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
import com.google.inject.Singleton;

import android.content.res.Resources;

@Singleton
public class SyncCollectingParameter {
    private String log;
    private final Resources resources;

    @Inject
    public SyncCollectingParameter(Resources resources) {
        this.resources = resources;
        reset();
    }

    public void Log(int resId, Object... args) {
        Log(resources.getString(resId, args));
    }

    public void NestedLog(int resId, Object... args) {
        Log("  " + resources.getString(resId, args));
    }

    public void Log(String s) {
        this.log += s + "\n";
    }

    public String getLog() {
        return this.log;
    }

    public void reset() {
        this.log = "";
    }
}
