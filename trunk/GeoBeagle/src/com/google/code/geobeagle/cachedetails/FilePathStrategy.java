
package com.google.code.geobeagle.cachedetails;

public class FilePathStrategy {
    private static String replaceIllegalFileChars(String wpt) {
        return wpt.replaceAll("[<\\\\/:\\*\\?\">| \\t]", "_");
    }

    public String getPath(CharSequence gpxName, String wpt) {
        return CacheDetailsLoader.DETAILS_DIR + gpxName + "/"
                + String.valueOf(Math.abs(wpt.hashCode()) % 16) + "/"
                + replaceIllegalFileChars(wpt) + ".html";
    }
}
