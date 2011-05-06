
package com.google.code.geobeagle.cachedetails;

import java.io.IOException;

public interface CacheWriterOpener {
    public void open(String path, String wpt) throws IOException;
}
