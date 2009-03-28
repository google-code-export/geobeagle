package com.google.code.geobeagle.ui.di;

import com.google.code.geobeagle.GeoBeagle;
import com.google.code.geobeagle.ui.WebPageAndDetailsButtonEnabler;

import android.view.View;

public class WebPageAndDetailsButtonDI {

    public static WebPageAndDetailsButtonEnabler create(GeoBeagle geoBeagle, View cachePageButton,
            View detailsButton) {
        return new WebPageAndDetailsButtonEnabler(geoBeagle, cachePageButton, detailsButton);
    }

}
