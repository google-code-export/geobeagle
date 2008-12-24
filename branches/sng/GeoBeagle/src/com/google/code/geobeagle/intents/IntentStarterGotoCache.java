
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

public interface IntentStarterGotoCache {
    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination);
}
