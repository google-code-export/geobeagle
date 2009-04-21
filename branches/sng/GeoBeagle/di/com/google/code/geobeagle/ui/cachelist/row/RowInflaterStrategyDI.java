
package com.google.code.geobeagle.ui.cachelist.row;

import com.google.code.geobeagle.data.GeocacheVectors;

import android.view.LayoutInflater;
import android.view.View;

public class RowInflaterStrategyDI {
    public static RowInflaterStrategy create(LayoutInflater layoutInflater, View gpsWidgetRowView,
            GeocacheVectors geocacheVectors) {
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                layoutInflater, geocacheVectors);
        final GpsWidgetRowInflater gpsWidgetRowInflater = new GpsWidgetRowInflater(gpsWidgetRowView);
        return new RowInflaterStrategy(new RowInflater[] {
                geocacheSummaryRowInflater, gpsWidgetRowInflater
        });

    }
}
