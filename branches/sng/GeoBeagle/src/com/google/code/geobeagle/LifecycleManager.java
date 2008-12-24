
package com.google.code.geobeagle;

import com.google.code.geobeagle.ui.ErrorDisplayer;

public interface LifecycleManager {

    void onPause();

    void onResume(ErrorDisplayer errorDisplayer, String initialDestination);

}
