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

import android.content.res.Resources;

public class ActionStaticLabel {
    private final Resources mResources;
    private final int mLabelId;

    public ActionStaticLabel(Resources resources, int labelId) {
        super();
        mResources = resources;
        mLabelId = labelId;
    }

    public String getLabel() {
        return mResources.getString(mLabelId);
    }

    public String getLabel(Geocache geocache) {
        return getLabel();
    }

}
