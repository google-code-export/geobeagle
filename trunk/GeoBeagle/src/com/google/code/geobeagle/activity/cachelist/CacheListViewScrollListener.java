package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh.UpdateFlag;
import com.google.inject.Inject;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class CacheListViewScrollListener implements OnScrollListener {
    private final UpdateFlag mUpdateFlag;

    @Inject
    public CacheListViewScrollListener(UpdateFlag updateFlag) {
        mUpdateFlag = updateFlag;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mUpdateFlag.setUpdatesEnabled(scrollState == SCROLL_STATE_IDLE);
    }
}