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

package com.google.code.geobeagle.activity.cachelist.actions.context;

import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.inject.Inject;

import android.content.Context;
import android.content.Intent;

public class ContextActionView implements ContextAction {
    private final Context mContext;
    private final GeocacheVectors mGeocacheVectors;

    @Inject
    public ContextActionView(GeocacheVectors geocacheVectors, Context context) {
        mGeocacheVectors = geocacheVectors;
        mContext = context;
    }

    @Override
    public void act(int position) {
        Intent intent = new Intent(mContext,
                com.google.code.geobeagle.activity.main.CompassActivity.class);
        intent.putExtra("geocache", mGeocacheVectors.get(position).getGeocache()).setAction(
                GeocacheListController.SELECT_CACHE);
        mContext.startActivity(intent);
    }
}
