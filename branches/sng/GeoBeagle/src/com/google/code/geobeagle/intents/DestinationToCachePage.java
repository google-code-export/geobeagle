
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

public class DestinationToCachePage implements DestinationToUri {
    private final ResourceProvider mResourceProvider;

    public DestinationToCachePage(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public String convert(Destination destination) {
        return String.format(mResourceProvider.getString(R.string.cache_page_url), destination
                .getDescription());
    }

}
