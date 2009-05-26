
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.ui.GpsStatusWidget.GpsStatusWidgetDelegate;
import com.google.code.geobeagle.ui.GpsStatusWidget.UpdateGpsWidgetRunnable;

import android.os.Handler;

public class UpdateGpsWidgetRunnableDI {
    public static UpdateGpsWidgetRunnable create(GpsStatusWidgetDelegate gpsStatusWidgetDelegate) {
        final Handler handler = new Handler();
        return new UpdateGpsWidgetRunnable(gpsStatusWidgetDelegate, handler);
    }
}
