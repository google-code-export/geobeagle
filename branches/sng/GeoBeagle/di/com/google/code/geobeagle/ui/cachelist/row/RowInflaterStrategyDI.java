
package com.google.code.geobeagle.ui.cachelist.row;

import com.google.code.geobeagle.data.GeocacheVectors;

import android.view.LayoutInflater;

public class RowInflaterStrategyDI {
    public static RowInflaterStrategy create(LayoutInflater layoutInflater,
            GeocacheVectors geocacheVectors) {
        final GeocacheSummaryRowInflater geocacheSummaryRowInflater = new GeocacheSummaryRowInflater(
                layoutInflater, geocacheVectors);
        return new RowInflaterStrategy(new RowInflater[] {
            geocacheSummaryRowInflater
        });

    }
}
