
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.cachedetails.StringWriterWrapper;
import com.google.inject.Inject;

import java.io.IOException;

public class CacheTagsToUrl extends CachePersisterFacade {
    private final StringWriterWrapper stringWriterWrapper;

    @Inject
    CacheTagsToUrl(StringWriterWrapper stringWriterWrapper) {
        this.stringWriterWrapper = stringWriterWrapper;
    }

    @Override
    public void url(String text) {
        try {
            stringWriterWrapper.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
