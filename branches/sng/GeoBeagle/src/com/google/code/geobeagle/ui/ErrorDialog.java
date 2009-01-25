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

package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.ResourceProvider;

import android.app.AlertDialog;

public class ErrorDialog {

    private final AlertDialog mAlertDialog;
    private final ResourceProvider mResourceProvider;

    public ErrorDialog(AlertDialog alertDialog, ResourceProvider resourceProvider) {
        mAlertDialog = alertDialog;
        mResourceProvider = resourceProvider;
    }

    public void show(int error) {
        mAlertDialog.setMessage(mResourceProvider.getString(error));
        mAlertDialog.show();
    }

    public void show(String msg) {
        mAlertDialog.setMessage(msg);
        mAlertDialog.show();
        
    }

}
