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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.activity.compass.view.OnClickListenerCacheDetails;
import com.google.code.geobeagle.activity.map.OnClickListenerMapPage;
import com.google.inject.Inject;

import android.app.Activity;

class CompassClickListenerSetter {
    private final OnClickListenerCacheDetails onClickListenerCacheDetails;
    private final OnClickListenerNavigate onClickListenerNavigate;
    private final OnClickListenerMapPage onClickListenerMapPage;

    @Inject
    public CompassClickListenerSetter(OnClickListenerCacheDetails onClickListenerCacheDetails,
            OnClickListenerNavigate onClickListenerNavigate,
            OnClickListenerMapPage onClickListenerMapPage) {
        this.onClickListenerCacheDetails = onClickListenerCacheDetails;
        this.onClickListenerNavigate = onClickListenerNavigate;
        this.onClickListenerMapPage = onClickListenerMapPage;
    }

    void setListeners(HasViewById hasViewById, Activity activity) {
        hasViewById.findViewById(R.id.cache_details)
                .setOnClickListener(onClickListenerCacheDetails);
        hasViewById.findViewById(id.navigate).setOnClickListener(onClickListenerNavigate);
        hasViewById.findViewById(id.menu_log_find).setOnClickListener(
                new LogFindClickListener(activity, id.menu_log_find));
        hasViewById.findViewById(id.menu_log_dnf).setOnClickListener(
                new LogFindClickListener(activity, id.menu_log_dnf));
        hasViewById.findViewById(R.id.maps).setOnClickListener(onClickListenerMapPage);

    }
}
