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

package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.EditCacheActivity;

import android.content.Context;
import android.content.Intent;

public class CacheActionEdit implements CacheAction {
    private final Context mContext;

    public CacheActionEdit(Context context) {
        mContext = context;
    }

    @Override
    public void act(Geocache cache) {
        Intent intent = new Intent(mContext, EditCacheActivity.class);
        intent.putExtra("geocache", cache);
        mContext.startActivity(intent);
    }

    @Override
    public String getLabel() {
        return mContext.getResources().getString(R.string.menu_edit_geocache);
    }
}
