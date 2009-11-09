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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListAdapter;
//import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CacheListView extends ListView {

    public static class ScrollListener implements OnScrollListener {
        private final CacheListAdapter mUpdateFlag;

        public ScrollListener(CacheListAdapter updateFlag) {
            mUpdateFlag = updateFlag;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mUpdateFlag.enableUpdates(scrollState == SCROLL_STATE_IDLE);
        }
    }

    // If these constructors aren't here, Android throws
    // java.lang.NoSuchMethodException: CacheListView(Context,AttributeSet)
    public CacheListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CacheListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CacheListView(Context context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

}
