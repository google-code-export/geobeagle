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

package com.google.code.geobeagle.ui.cachelist;

import com.google.code.geobeagle.data.GeocacheVectors;

import android.content.Context;
import android.content.Intent;

public class ViewAction implements Action {
    private final Context mContext;
    private final Intent mIntent;
    private GeocacheVectors mGeocacheVectors;

    ViewAction(GeocacheVectors geocacheVectors, Context context, Intent intent) {
        mGeocacheVectors = geocacheVectors;
        mContext = context;
        mIntent = intent;
    }

    public void act(int position, GeocacheListAdapter geocacheListAdapter) {
        mIntent.putExtra("geocache", mGeocacheVectors.get(position).getGeocache()).setAction(
                GeocacheListDelegate.SELECT_CACHE);
        mContext.startActivity(mIntent);
    }
}
