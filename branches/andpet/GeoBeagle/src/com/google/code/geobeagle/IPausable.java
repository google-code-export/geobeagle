package com.google.code.geobeagle;

/** Allow objects that need to be paused/resumed to be sent as parameters 
 * without requiring their specific type to be exposed.
 */
public interface IPausable {
    void onPause();
    void onResume();
}
