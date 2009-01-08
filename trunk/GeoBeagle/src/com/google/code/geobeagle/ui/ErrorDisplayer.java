
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
