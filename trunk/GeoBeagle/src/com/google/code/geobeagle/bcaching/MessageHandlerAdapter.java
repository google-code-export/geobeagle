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

package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.bcaching.progress.ProgressHandler;
import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;
import com.google.code.geobeagle.xmlimport.MessageHandlerInterface;
import com.google.inject.Inject;
import com.google.inject.Injector;

import android.os.Message;

public class MessageHandlerAdapter implements MessageHandlerInterface {

    private final ProgressManager progressManager;
    private final ProgressHandler handler;
    private String waypoint;

    public MessageHandlerAdapter(ProgressHandler handler, ProgressManager progressManager) {
        this.handler = handler;
        this.progressManager = progressManager;
        waypoint = "";
    }

    @Inject
    public MessageHandlerAdapter(Injector injector) {
        this.handler = injector.getInstance(ProgressHandler.class);
        this.progressManager = injector.getInstance(ProgressManager.class);
        waypoint = "";
    }

    @Override
    public void abortLoad() {
    }

    @Override
    public void deletingCacheFiles() {
    }

    @Override
    public void handleMessage(Message msg) {
    }

    @Override
    public void loadComplete() {
    }

    @Override
    public void start(CacheListRefresh cacheListRefresh) {
    }

    @Override
    public void updateName(String name) {
        if (waypoint.startsWith("GC"))
            progressManager.incrementProgress();
        progressManager.update(handler, ProgressMessage.SET_FILE,
                progressManager.getCurrentProgress(), waypoint + " - " + name);
    }

    @Override
    public void updateSource(String text) {
    }

    @Override
    public void updateStatus(String status) {
    }

    @Override
    public void updateWaypointId(String wpt) {
        this.waypoint = wpt;
    }
}
