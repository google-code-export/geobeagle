package com.google.code.geobeagle.xmlimport;

@SuppressWarnings("serial")
public class ImportException extends Exception {
    private int error;

    public ImportException(int error) {
        super();
        this.error = error;
    }

    public int getError() {
        return error;
    }
}
