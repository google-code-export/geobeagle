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

package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.inject.Inject;

import android.app.Activity;
import android.widget.EditText;

public class OnClickOkFactory {
    private final CacheLogger cacheLogger;
    private final Activity activity;
    private final HasGeocache hasGeocache;

    @Inject
    public OnClickOkFactory(Activity activity, CacheLogger cacheLogger, HasGeocache hasGeocache) {
        this.activity = activity;
        this.cacheLogger = cacheLogger;
        this.hasGeocache = hasGeocache;
    }

    public OnClickOk create(EditText editText, boolean dnf) {
        return new OnClickOk(hasGeocache.get(activity).getId(), editText, cacheLogger, dnf);
    }
}
