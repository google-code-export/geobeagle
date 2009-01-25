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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDisplayer {
    private final Context context;

    public ErrorDisplayer(Context context) {
        this.context = context;
    }

    public void displayError(int resourceId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        final Builder setMessage = setMessage(alertDialogBuilder, resourceId);
        setMessage.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialogBuilder.create().show();
    }

    private Builder setMessage(AlertDialog.Builder alertDialogBuilder, int resourceId) {
        return alertDialogBuilder.setMessage(resourceId);
    }

    public void displayError(String string) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        final Builder setMessage = setMessage(alertDialogBuilder, string);
        setMessage.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialogBuilder.create().show();
    }

    private Builder setMessage(AlertDialog.Builder alertDialogBuilder, String string) {
        return alertDialogBuilder.setMessage(string);
    }
}
