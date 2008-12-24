
package com.google.code.geobeagle.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDisplayerImpl implements ErrorDisplayer {
    private final Context context;

    public ErrorDisplayerImpl(Context context) {
        this.context = context;
    }

    public void displayError(int resourceId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        alertDialogBuilder.setMessage(resourceId).setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertDialogBuilder.create().show();
    }
}
