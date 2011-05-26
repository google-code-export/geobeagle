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

package com.google.code.geobeagle.bcaching.progress;

import com.google.inject.Singleton;

import android.os.Message;
import android.util.Log;

@Singleton
public class ProgressManager {
    private int currentProgress;

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void update(ProgressHandler handler, ProgressMessage progressMessage, int arg) {
        Message.obtain(handler, progressMessage.ordinal(), arg, 0).sendToTarget();
    }

    public void update(ProgressHandler handler,
            ProgressMessage progressMessage,
            int arg1,
            String arg2) {
        if (!handler.hasMessages(progressMessage.ordinal()))
            Message.obtain(handler, progressMessage.ordinal(), arg1, 0, arg2).sendToTarget();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void incrementProgress() {
        Log.d("GeoBeagle", "incrementing Progress: " + currentProgress);
        currentProgress++;
    }
}
