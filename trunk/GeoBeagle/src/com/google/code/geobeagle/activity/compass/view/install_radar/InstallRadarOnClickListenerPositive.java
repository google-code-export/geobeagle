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

package com.google.code.geobeagle.activity.compass.view.install_radar;

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

class InstallRadarOnClickListenerPositive implements
        android.content.DialogInterface.OnClickListener {
    private final Activity mActivity;

    @Inject
    public InstallRadarOnClickListenerPositive(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mActivity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mActivity
                .getString(R.string.install_radar_uri))));
    }

}
