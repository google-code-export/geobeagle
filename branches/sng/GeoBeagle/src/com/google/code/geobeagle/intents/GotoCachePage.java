
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

public class GotoCachePage extends GotoCacheByViewingUri {
    private final ResourceProvider mResourceProvider;

    public GotoCachePage(ActivityStarter activityStarter, IntentFromActionUriFactory intentFromActionUriFactory,
            ResourceProvider resourceProvider) {
        super(activityStarter, intentFromActionUriFactory);
        mResourceProvider = resourceProvider;
    }

    protected String getUri(Destination destination) {
        return String.format(mResourceProvider.getString(R.string.cache_page_url), destination
                .getDescription());
    }
}
