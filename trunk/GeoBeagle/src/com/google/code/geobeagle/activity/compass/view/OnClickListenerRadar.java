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

package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.activity.compass.IntentStarterRadar;
import com.google.code.geobeagle.activity.compass.view.install_radar.InstallRadarAppDialog;
import com.google.inject.Inject;

import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickListenerRadar implements OnClickListener {
    private final IntentStarterRadar intentStarterRadar;
    private final InstallRadarAppDialog installRadarAppDialog;

    @Inject
    OnClickListenerRadar(IntentStarterRadar intentStarterRadar,
            InstallRadarAppDialog installRadarAppDialog) {
        this.intentStarterRadar = intentStarterRadar;
        this.installRadarAppDialog = installRadarAppDialog;
    }

    @Override
    public void onClick(View arg0) {
        try {
            intentStarterRadar.startIntent();
        } catch (final ActivityNotFoundException e) {
            installRadarAppDialog.showInstallRadarDialog();
        }
    }

}
