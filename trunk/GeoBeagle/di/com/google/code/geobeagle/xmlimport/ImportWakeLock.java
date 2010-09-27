package com.google.code.geobeagle.xmlimport;

import com.google.inject.Inject;

import roboguice.inject.ContextScoped;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

@ContextScoped
class ImportWakeLock {

    private final WakeLock wakeLock;

    @Inject
    ImportWakeLock(Context context) {
        PowerManager powerManager = (PowerManager)context
                .getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Importing");
    }

    public void acquire(long duration) {
        wakeLock.acquire(duration);
    }

}