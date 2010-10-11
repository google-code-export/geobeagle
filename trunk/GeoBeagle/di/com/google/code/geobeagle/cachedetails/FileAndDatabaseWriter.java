
package com.google.code.geobeagle.cachedetails;

import com.google.inject.Inject;

import java.io.IOException;

public class FileAndDatabaseWriter implements Writer {

    private final WriterWrapper writerWrapper;
    private final DetailsDatabaseWriter detailsDatabaseWriter;

    @Inject
    FileAndDatabaseWriter(WriterWrapper writerWrapper, DetailsDatabaseWriter detailsDatabaseWriter) {
        this.writerWrapper = writerWrapper;
        this.detailsDatabaseWriter = detailsDatabaseWriter;
    }

    @Override
    public void close() throws IOException {
        writerWrapper.close();
        detailsDatabaseWriter.close();
    }

    @Override
    public void open(String path) throws IOException {
        writerWrapper.open(path);
        detailsDatabaseWriter.open(path);
    }

    @Override
    public void write(String str) throws IOException {
        writerWrapper.write(str);
        detailsDatabaseWriter.write(str);
    }

    @Override
    public boolean isOpen() {
        return writerWrapper.isOpen() || detailsDatabaseWriter.isOpen();
    }

    @Override
    public void mkdirs(String path) {
        writerWrapper.mkdirs(path);
        detailsDatabaseWriter.mkdirs(path);
    }

}
