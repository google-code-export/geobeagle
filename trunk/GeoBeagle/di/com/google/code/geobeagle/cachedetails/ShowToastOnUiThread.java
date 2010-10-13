package com.google.code.geobeagle.cachedetails;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Context;

class ShowToastOnUiThread {
    @Inject
    public ShowToastOnUiThread(Provider<Context> contextProvider,
            Provider<Activity> activityProvider) {
        this.contextProvider = contextProvider;
        this.activityProvider = activityProvider;
    }

    private final Provider<Context> contextProvider;
    private final Provider<Activity> activityProvider;

    public void showToast(int msg, int length) {
        Runnable showToast = new ShowToastRunnable(contextProvider.get(), msg, length);
        activityProvider.get().runOnUiThread(showToast);
    }
}