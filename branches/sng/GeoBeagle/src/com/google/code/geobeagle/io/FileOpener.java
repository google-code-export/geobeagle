
package com.google.code.geobeagle.io;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileOpener {

    public static class FileReaderFactory {
        public FileReader create() throws FileNotFoundException {
            return new FileReader("/sdcard/caches.gpx");
        }
    }

    public FileReader open(FileReaderFactory fileReaderFactory) {
        try {
            return fileReaderFactory.create();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
