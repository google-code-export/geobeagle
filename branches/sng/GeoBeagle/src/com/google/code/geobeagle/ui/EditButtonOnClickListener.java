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

package com.google.code.geobeagle.ui;


import com.google.code.geobeagle.ui.di.EditCacheActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EditButtonOnClickListener implements OnClickListener {
    private final Activity mActivity;
    private final TextView mGeocache;

    public EditButtonOnClickListener(Activity activity, TextView geocache) {
        mActivity = activity;
        mGeocache = geocache;
    }

    public void onClick(View arg0) {
        Intent intent = new Intent(mActivity, EditCacheActivity.class);
        intent.putExtra("cache", mGeocache.getText());
        mActivity.startActivityForResult(intent, 0);
    }
}
