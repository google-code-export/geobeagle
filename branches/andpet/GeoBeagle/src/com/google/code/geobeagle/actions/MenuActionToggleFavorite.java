package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.Labels;
import com.google.code.geobeagle.database.DbFrontend;

public class MenuActionToggleFavorite implements MenuAction {
    private final DbFrontend mDbFrontend;
    private final Geocache mGeocache;
    
    public MenuActionToggleFavorite(DbFrontend dbFrontend, Geocache cache) {
        mDbFrontend = dbFrontend;
        mGeocache = cache;
    }
    
    @Override
    public void act() {
        boolean isFavorite = mDbFrontend.geocacheHasLabel(mGeocache.getId(), 
                Labels.FAVORITES);
        if (isFavorite)
            mDbFrontend.unsetGeocacheLabel(mGeocache.getId(), Labels.FAVORITES);
        else
            mDbFrontend.setGeocacheLabel(mGeocache.getId(), Labels.FAVORITES);
    }

    @Override
    public String getLabel() {
        boolean isFavorite = mDbFrontend.geocacheHasLabel(mGeocache.getId(), 
                Labels.FAVORITES);
        return isFavorite ? "Remove from Favorites" : "Add to Favorites";
    }

}
