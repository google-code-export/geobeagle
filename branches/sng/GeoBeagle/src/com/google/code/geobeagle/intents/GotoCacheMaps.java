
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

public class GotoCacheMaps extends GotoCacheByViewingUri {
    private final ResourceProvider mResourceProvider;

    public GotoCacheMaps(ActivityStarter activityStarter,
            IntentFromActionUriFactory intentFromActionUriFactory, ResourceProvider resourceProvider) {
        super(activityStarter, intentFromActionUriFactory);
        mResourceProvider = resourceProvider;
    }

    protected String getUri(Destination destination) {
        // "geo:%1$.5f,%2$.5f?name=cachename"
        return String.format(mResourceProvider.getString(R.string.map_intent), destination
                .getLatitude(), destination.getLongitude(), destination.getDescription());
    }
}
