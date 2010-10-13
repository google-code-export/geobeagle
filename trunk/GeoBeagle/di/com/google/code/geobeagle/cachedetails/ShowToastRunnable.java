package com.google.code.geobeagle.cachedetails;

import android.content.Context;
import android.widget.Toast;

class ShowToastRunnable implements Runnable {
    private final Context context;
    private final int msg;
    private final int length;

    public ShowToastRunnable(Context context, int msg, int length) {
        this.context = context;
        this.msg = msg;
        this.length = length;
    }

    @Override
    public void run() {
        Toast.makeText(context, msg, length).show();
    }
}