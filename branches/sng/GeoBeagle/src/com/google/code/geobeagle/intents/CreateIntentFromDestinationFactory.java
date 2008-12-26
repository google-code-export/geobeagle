
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;

import android.content.Intent;

public class CreateIntentFromDestinationFactory {
    private final IntentFromActionUriFactory mIntentFromActionUriFactory;
    private final DestinationToUri mDestinationToUri;

    public CreateIntentFromDestinationFactory(
            IntentFromActionUriFactory intentFromActionUriFactory, DestinationToUri destinationToUri) {
        mIntentFromActionUriFactory = intentFromActionUriFactory;
        mDestinationToUri = destinationToUri;
    }

    public Intent createIntent(Destination destination) {
        return mIntentFromActionUriFactory.createIntent(Intent.ACTION_VIEW, mDestinationToUri
                .convert(destination));
    }

}
