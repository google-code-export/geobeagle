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

package com.google.code.geobeagle.cachedetails;

import android.content.Context;
import android.widget.Toast;

class ShowToastRunnable implements Runnable {
    private final Context context;
    private final int msg;
    private final int length;

    public ShowToastRunnable(Context context, int msg, int length) {
        this.context = context;
        this.msg = msg;
        this.length = length;
    }

    @Override
    public void run() {
        Toast.makeText(context, msg, length).show();
    }
}
