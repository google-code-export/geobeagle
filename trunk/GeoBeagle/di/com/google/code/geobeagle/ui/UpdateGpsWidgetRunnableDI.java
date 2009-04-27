
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;

import android.os.Handler;

public class UpdateGpsWidgetRunnableDI {
    public static UpdateGpsWidgetRunnable create(GpsStatusWidget gpsStatusWidget) {
        Handler handler = new Handler();
        return new UpdateGpsWidgetRunnable(gpsStatusWidget, handler);
    }
}
