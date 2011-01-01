package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.cachedetails.FileDataVersionChecker;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

class CacheReaderFromFile {
    private final FileDataVersionChecker fileDataVersionChecker;
    private final FilePathStrategy filePathStrategy;

    CacheReaderFromFile(FileDataVersionChecker fileDataVersionChecker,
            FilePathStrategy filePathStrategy) {
        this.fileDataVersionChecker = fileDataVersionChecker;
        this.filePathStrategy = filePathStrategy;
    }

    Reader getReader(CharSequence sourceName, CharSequence cacheId)
            throws CacheLoaderException {
        String path = filePathStrategy.getPath(sourceName, cacheId.toString(), "gpx");
        File file = new File(path);
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new CacheLoaderException(R.string.error_cant_read_sdroot, state);
        }
        String absolutePath = file.getAbsolutePath();
        try {
            return new BufferedReader(new FileReader(absolutePath));
        } catch (FileNotFoundException e) {
            int error = fileDataVersionChecker.needsUpdating() ? R.string.error_details_file_version
                    : R.string.error_opening_details_file;
            throw new CacheLoaderException(error, e.getMessage());
        }
    }
}