
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public abstract class GotoCacheByViewingUri extends GotoCache {
    private final IntentFromActionUriFactory mIntentFromActionUriFactory;

    protected GotoCacheByViewingUri(ActivityStarter activityStarter,
            IntentFromActionUriFactory intentFromActionUriFactory) {
        super(activityStarter);
        mIntentFromActionUriFactory = intentFromActionUriFactory;
    }

    @Override
    protected Intent createIntent(Destination destination) {
        return mIntentFromActionUriFactory.createIntent(Intent.ACTION_VIEW, getUri(destination));
    }

    abstract String getUri(Destination destination);
}
