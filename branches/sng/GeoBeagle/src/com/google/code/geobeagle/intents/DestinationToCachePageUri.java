
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

public class DestinationToCachePageUri implements DestinationToUri {
    private final ResourceProvider mResourceProvider;

    public DestinationToCachePageUri(ResourceProvider resourceProvider) {
        mResourceProvider = resourceProvider;
    }

    public String convert(Destination destination) {
        return String.format(mResourceProvider.getString(R.string.cache_page_url), destination
                .getDescription());
    }

}
