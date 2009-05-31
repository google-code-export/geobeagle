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

package com.google.code.geobeagle.mainactivity.ui;

import com.google.code.geobeagle.mainactivity.intents.IntentStarter;

import android.app.Activity;

public class OnCacheButtonClickListenerBuilder {
    private final Activity mActivity;
    private final ErrorDisplayer mErrorDisplayer;

    public OnCacheButtonClickListenerBuilder(Activity activity, ErrorDisplayer errorDisplayer) {
        mErrorDisplayer = errorDisplayer;
        mActivity = activity;
    }

    public void set(int id, IntentStarter intentStarter, String errorString) {
        mActivity.findViewById(id).setOnClickListener(new CacheButtonOnClickListener(
                intentStarter, errorString, mErrorDisplayer));
    }
}
