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

package com.google.code.geobeagle.activity.cachelist.presenter.filter;

import com.google.inject.Inject;

import android.os.Handler;
import android.os.Message;

public class UpdateFilterHandler extends Handler {
    private final UpdateFilterMediator updateFilterMediator;

    @Inject
    UpdateFilterHandler(UpdateFilterMediator updateFilterMediator) {
        this.updateFilterMediator = updateFilterMediator;
    }

    @Override
    public void handleMessage(Message msg) {
        UpdateFilterMessages updateFilterMessage = UpdateFilterMessages.fromOrd(msg.what);
            updateFilterMessage.handleMessage(updateFilterMediator, msg.obj);
    }

    private void sendMessage(UpdateFilterMessages updateFilterMessage) {
        sendMessage(updateFilterMessage, null);
    }

    private void sendMessage(UpdateFilterMessages updateFilterMessage, String prompt) {
        sendMessage(obtainMessage(updateFilterMessage.ordinal(), prompt));
    }

    public void endFiltering() {
        sendMessage(UpdateFilterMessages.END_FILTERING);
    }

    public void setProgressMessage(String string) {
        sendMessage(UpdateFilterMessages.SET_PROGRESS_MESSAGE, string);
    }
}
