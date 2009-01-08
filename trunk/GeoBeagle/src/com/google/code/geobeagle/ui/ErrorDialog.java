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
