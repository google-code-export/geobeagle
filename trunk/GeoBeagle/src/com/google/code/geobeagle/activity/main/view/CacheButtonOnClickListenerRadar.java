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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class CacheButtonOnClickListenerRadar implements OnClickListener {
    private final IntentStarter mIntentStarter;
    private final Activity mActivity;

    static class InstallRadarOnClickListenerNegative implements
            android.content.DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }

    }

    static class InstallRadarOnClickListenerPositive implements
            android.content.DialogInterface.OnClickListener {
        private final Activity mActivity;

        public InstallRadarOnClickListenerPositive(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mActivity
                    .getString(R.string.install_radar_uri))));
        }

    }

    public CacheButtonOnClickListenerRadar(Activity activity, IntentStarter intentStarter) {
        mIntentStarter = intentStarter;
        mActivity = activity;
    }

    @Override
    public void onClick(View arg0) {
        try {
            mIntentStarter.startIntent();
        } catch (final ActivityNotFoundException e) {
            final Builder alertDialogBuilder = new Builder(mActivity);
            alertDialogBuilder.setMessage(R.string.ask_install_radar_app);
            alertDialogBuilder.setPositiveButton(R.string.install_radar,
                    new InstallRadarOnClickListenerPositive(mActivity));
            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new InstallRadarOnClickListenerNegative());
            alertDialogBuilder.create().show();
        }
    }

}
