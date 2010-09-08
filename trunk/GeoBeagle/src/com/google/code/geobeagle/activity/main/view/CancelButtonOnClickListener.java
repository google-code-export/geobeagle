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
package com.google.code.geobeagle.activity.main.view;

import com.google.inject.Inject;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class CancelButtonOnClickListener implements OnClickListener {
    private final Activity activity;

    @Inject
    public CancelButtonOnClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        // TODO: replace magic number.
        activity.setResult(-1, null);
        activity.finish();
    }
}