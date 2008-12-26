
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public class GotoCacheByViewingUri extends GotoCache {
    private final CreateIntentFromDestinationFactory mCreateIntentFromDestinationFactory;

    public GotoCacheByViewingUri(ActivityStarter activityStarter,
            CreateIntentFromDestinationFactory createIntentFromDestinationFactory) {
        super(activityStarter);
        mCreateIntentFromDestinationFactory = createIntentFromDestinationFactory;
    }

    protected Intent createIntent(Destination destination) {
        return mCreateIntentFromDestinationFactory.createIntent(destination);
    }
}
