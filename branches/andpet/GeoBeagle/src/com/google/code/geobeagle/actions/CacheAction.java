package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;

public interface CacheAction {
    public void act(Geocache cache);

    /** Returns the text to show to the user for this action. May change. */
    public String getLabel();
}
