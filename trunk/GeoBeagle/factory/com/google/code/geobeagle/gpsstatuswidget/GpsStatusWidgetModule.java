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

package com.google.code.geobeagle.gpsstatuswidget;

import com.google.code.geobeagle.R;
import com.google.inject.Provides;

import roboguice.config.AbstractAndroidModule;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GpsStatusWidgetModule extends AbstractAndroidModule {
    static InflatedGpsStatusWidget inflatedGpsStatusWidgetCacheList;

    @Override
    protected void configure() {
    }

    @Provides
    InflatedGpsStatusWidget providesInflatedGpsStatusWidget(Activity activity) {
        Context context = activity.getApplicationContext();
        InflatedGpsStatusWidget searchOnlineWidget = getInflatedGpsStatusWidgetSearchOnline(activity);
        if (searchOnlineWidget != null)
            return searchOnlineWidget;

        return getInflatedGpsStatusWidgetCacheList(context);
    }

    InflatedGpsStatusWidget getInflatedGpsStatusWidgetCacheList(Context context) {
        if (inflatedGpsStatusWidgetCacheList != null)
            return inflatedGpsStatusWidgetCacheList;

        inflatedGpsStatusWidgetCacheList = new InflatedGpsStatusWidget(context);
        LinearLayout inflatedGpsStatusWidgetParent = new LinearLayout(context);

        inflatedGpsStatusWidgetParent.addView(inflatedGpsStatusWidgetCacheList,
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inflatedGpsStatusWidgetCacheList.setTag(inflatedGpsStatusWidgetParent);
        return inflatedGpsStatusWidgetCacheList;
    }

    InflatedGpsStatusWidget getInflatedGpsStatusWidgetSearchOnline(Activity activity) {
        return (InflatedGpsStatusWidget)activity.findViewById(R.id.gps_widget_view);
    }
}
