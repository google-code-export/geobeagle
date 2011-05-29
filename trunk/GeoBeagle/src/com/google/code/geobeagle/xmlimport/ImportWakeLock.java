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

package com.google.code.geobeagle.xmlimport;

import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

@ContextScoped
class ImportWakeLock {

    private final WakeLock wakeLock;

    @Inject
    ImportWakeLock(Context context) {
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
    }

    public void acquire(long duration) {
        wakeLock.acquire(duration);
    }

}
