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

package com.google.code.geobeagle.activity;

import com.google.code.geobeagle.activity.compass.view.EditCacheActivityDelegate;
import com.google.inject.Injector;

import roboguice.activity.GuiceActivity;

import android.os.Bundle;

public class EditCacheActivity extends GuiceActivity {
    private EditCacheActivityDelegate editCacheActivityDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector injector = getInjector();
        editCacheActivityDelegate = injector.getInstance(EditCacheActivityDelegate.class);

        editCacheActivityDelegate.onCreate();
    }

    @Override
    protected void onPause() {
        editCacheActivityDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editCacheActivityDelegate.onResume();
    }
}
