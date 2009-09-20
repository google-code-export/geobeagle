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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;

import android.app.Activity;
import android.content.Intent;

public class MenuActionCacheList extends MenuActionBase {
    private Activity mActivity;

    public MenuActionCacheList(Activity activity) {
        super(R.string.menu_cache_list);
        mActivity = activity;
    }

    @Override
    public void act() {
        mActivity.startActivity(new Intent(mActivity, CacheListActivity.class));
    }
}
