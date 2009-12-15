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

import android.app.Activity;
import android.content.res.Resources;

//Could be changed into a CacheAction
public class MenuActionLogDnf extends ActionStaticLabel implements MenuAction {
    private final Activity mActivity;

    public MenuActionLogDnf(Activity activity, Resources resources) {
        super(resources, R.string.menu_log_dnf);
        mActivity = activity;
    }

    @Override
    public void act() {
        mActivity.showDialog(R.id.menu_log_dnf);
    }
}
