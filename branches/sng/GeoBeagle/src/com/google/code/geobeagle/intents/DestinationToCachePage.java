
package com.google.code.geobeagle.intents;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ContentSelector;

/*
 * Convert a Destination to the cache page url.
 */
public class DestinationToCachePage implements DestinationToUri {
    private final ResourceProvider mResourceProvider;

    public DestinationToCachePage(ResourceProvider resourceProvider, ContentSelector contentSelector) {
        mResourceProvider = resourceProvider;
    }

    public String convert(Destination destination) {
        return String.format(mResourceProvider.getStringArray(R.array.cache_page_url)[destination
                .getContentIndex()], destination.getId());
    }

}
