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

package com.google.code.geobeagle.activity.cachelist.presenter;

import com.google.code.geobeagle.activity.cachelist.CacheListDelegateDI;
import com.google.code.geobeagle.database.FilterNearestCaches;
import com.google.inject.Inject;

import android.app.Activity;
import android.widget.TextView;

public class TitleUpdater {
    private final FilterNearestCaches mFilterNearestCaches;
    private final Activity mActivity;
    private final ListTitleFormatter mListTitleFormatter;
    private final CacheListDelegateDI.Timing mTiming;

    public TitleUpdater(Activity activity, FilterNearestCaches filterNearestCaches,
            ListTitleFormatter listTitleFormatter, CacheListDelegateDI.Timing timing) {
        mActivity = activity;
        mFilterNearestCaches = filterNearestCaches;
        mListTitleFormatter = listTitleFormatter;
        mTiming = timing;
    }

    public void update(int sqlCount, int nearestCachesCount) {
        mActivity.setTitle(mActivity.getString(mFilterNearestCaches.getTitleText(),
                nearestCachesCount, sqlCount));
        if (0 == nearestCachesCount) {
            TextView textView = (TextView)mActivity.findViewById(android.R.id.empty);
            textView.setText(mListTitleFormatter.getBodyText(sqlCount));
        }
        mTiming.lap("update title time");
    }
}
