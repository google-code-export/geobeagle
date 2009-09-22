package com.google.code.geobeagle.actions;

import com.google.code.geobeagle.Geocache;

public interface CacheAction {
    public void act(Geocache cache);

    /** Must be the id of a resource string - used to set label */
    public int getId();
}
