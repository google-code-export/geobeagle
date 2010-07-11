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

import com.google.code.geobeagle.GeoBeaglePackageModule;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.GeoBeaglePackageModule.DialogOnClickListenerNOP;
import com.google.code.geobeagle.activity.main.GeoBeagleModule.IntentStarterRadar;
import com.google.code.geobeagle.activity.main.intents.IntentStarterGeo;
import com.google.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickListenerRadar implements OnClickListener {
    private final IntentStarterGeo mIntentStarterGeo;
    private final android.content.DialogInterface.OnClickListener mInstallRadarOnClickListenerNegative;
    private final InstallRadarOnClickListenerPositive mDialogInstallRadarOnClickListenerPositive;
    private final AlertDialog.Builder mDialogBuilder;

    static class InstallRadarOnClickListenerNegative implements
            android.content.DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }

    }

    static class InstallRadarOnClickListenerPositive implements
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

    @Inject
    public OnClickListenerRadar(
            AlertDialog.Builder dialogBuilder,
            @DialogOnClickListenerNOP android.content.DialogInterface.OnClickListener dialogOnClickListenerNegative,
            InstallRadarOnClickListenerPositive dialogInstallRadarOnClickListenerPositive,
            @IntentStarterRadar IntentStarterGeo intentStarter) {
        mIntentStarterGeo = intentStarter;
        mInstallRadarOnClickListenerNegative = dialogOnClickListenerNegative;
        mDialogInstallRadarOnClickListenerPositive = dialogInstallRadarOnClickListenerPositive;
        mDialogBuilder = dialogBuilder;
    }

    @Override
    public void onClick(View arg0) {
        try {
            mIntentStarterGeo.startIntent();
        } catch (final ActivityNotFoundException e) {
            mDialogBuilder.setMessage(R.string.ask_install_radar_app);
            mDialogBuilder.setPositiveButton(R.string.install_radar,
                    mDialogInstallRadarOnClickListenerPositive);
            mDialogBuilder.setNegativeButton(R.string.cancel,
                    mInstallRadarOnClickListenerNegative);
            mDialogBuilder.create().show();
        }
    }

}
