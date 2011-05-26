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

package com.google.code.geobeagle.activity.compass.menuactions;

import com.google.code.geobeagle.activity.compass.intents.IntentStarter;
import com.google.code.geobeagle.activity.compass.view.install_radar.InstallRadarAppDialog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;

public class NavigateOnClickListener implements DialogInterface.OnClickListener {
    private final IntentStarter[] intentStarters;
    private final InstallRadarAppDialog installRadarAppDialog;

    public NavigateOnClickListener(IntentStarter[] intentStarters,
            InstallRadarAppDialog installRadarAppDialog) {
        this.intentStarters = intentStarters;
        this.installRadarAppDialog = installRadarAppDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            intentStarters[which].startIntent();
        } catch (final ActivityNotFoundException e) {
            installRadarAppDialog.showInstallRadarDialog();
        }
    }
}
