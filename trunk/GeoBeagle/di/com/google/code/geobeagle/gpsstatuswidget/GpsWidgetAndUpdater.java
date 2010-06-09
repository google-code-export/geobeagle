
package com.google.code.geobeagle.gpsstatuswidget;

import com.google.inject.Inject;

public class GpsWidgetAndUpdater {
    private final GpsStatusWidgetDelegate mGpsStatusWidgetDelegate;
    private final UpdateGpsWidgetRunnable mUpdateGpsRunnable;

    @Inject
    public GpsWidgetAndUpdater(UpdateGpsWidgetRunnable updateGpsRunnable,
            GpsStatusWidgetDelegate gpsStatusWidgetDelegate) {
        mUpdateGpsRunnable = updateGpsRunnable;
        mGpsStatusWidgetDelegate = gpsStatusWidgetDelegate;
    }

    public GpsStatusWidgetDelegate getGpsStatusWidgetDelegate() {
        return mGpsStatusWidgetDelegate;
    }

    public UpdateGpsWidgetRunnable getUpdateGpsWidgetRunnable() {
        return mUpdateGpsRunnable;
    }
}
