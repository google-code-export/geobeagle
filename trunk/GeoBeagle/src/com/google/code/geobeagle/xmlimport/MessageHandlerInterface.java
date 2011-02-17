
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;

import android.os.Message;

public interface MessageHandlerInterface {

    public abstract void abortLoad();

    public abstract void handleMessage(Message msg);

    public abstract void loadComplete();

    public abstract void start(CacheListRefresh cacheListRefresh);

    public abstract void updateName(String name);

    public abstract void updateSource(String text);

    public abstract void updateWaypointId(String wpt);

    public abstract void updateStatus(String status);

    public abstract void deletingCacheFiles();
}
