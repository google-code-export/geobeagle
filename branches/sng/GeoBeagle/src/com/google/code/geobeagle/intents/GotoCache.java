
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

public interface GotoCache {
    public void startIntent(ActivityStarter activityStarter, IntentFactory intentFactory,
            Destination destination);
}
