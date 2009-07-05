
package com.google.code.geobeagle.activity;

public enum ActivityType {
    CACHE_LIST(1), NONE(0), SEARCH_ONLINE(2), VIEW_CACHE(3);

    int mIx;

    ActivityType(int i) {
        mIx = i;
    }

    int toInt() {
        return mIx;
    }
}
