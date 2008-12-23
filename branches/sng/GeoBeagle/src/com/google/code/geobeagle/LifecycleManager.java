
package com.google.code.geobeagle;

public interface LifecycleManager {

    void onPause();

    void onResume(ErrorDisplayer errorDisplayer, String initialDestination);

}
