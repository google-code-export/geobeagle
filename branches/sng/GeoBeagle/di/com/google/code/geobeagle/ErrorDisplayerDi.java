package com.google.code.geobeagle;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ErrorDisplayerDi {

    static public ErrorDisplayer createErrorDisplayer(Activity activity) {
        final OnClickListener mOnClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        return new ErrorDisplayer(activity, mOnClickListener);
    }

}
