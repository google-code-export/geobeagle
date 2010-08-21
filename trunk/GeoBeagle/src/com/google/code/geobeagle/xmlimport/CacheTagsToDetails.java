
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsWriter;
import com.google.inject.Inject;

import java.io.IOException;

public class CacheTagsToDetails implements ICachePersisterFacade {

    private final CacheDetailsWriter cacheDetailsWriter;
    private boolean encrypted;

    @Inject
    public CacheTagsToDetails(CacheDetailsWriter cacheDetailsWriter) {
        this.cacheDetailsWriter = cacheDetailsWriter;
    }

    @Override
    public void archived(String attributeValue) {
    }

    @Override
    public void available(String attributeValue) {
    }

    @Override
    public void cacheType(String text) throws IOException {
        cacheDetailsWriter.writeField("Type", text);
    }

    @Override
    public void close(boolean success) {
    }

    @Override
    public void container(String text) throws IOException {
        cacheDetailsWriter.writeField("Container", text);
    }

    @Override
    public void difficulty(String text) throws IOException {
        cacheDetailsWriter.writeField("Difficulty", text);
    }

    @Override
    public void end() {
    }

    @Override
    public void endCache(Source source) throws IOException {
        cacheDetailsWriter.close();
    }

    @Override
    public String getLastModified() {
        return null;
    }

    @Override
    public boolean gpxTime(String gpxTime) {
        return true;
    }

    @Override
    public void groundspeakName(String text) throws IOException {
        cacheDetailsWriter.writeName(text);
    }

    @Override
    public void hint(String text) throws IOException {
        cacheDetailsWriter.writeHint(text);
    }

    @Override
    public void lastModified(String trimmedText) {
    }

    @Override
    public void line(String text) throws IOException {
        cacheDetailsWriter.writeLine(text);
    }

    @Override
    public void logDate(String text) throws IOException {
        cacheDetailsWriter.writeLogDate(text);
    }

    @Override
    public void open(String path) throws IOException {
    }

    @Override
    public void start() {
    }

    @Override
    public void startCache() {
    }

    @Override
    public void symbol(String text) {
    }

    @Override
    public void terrain(String text) throws IOException {
        cacheDetailsWriter.writeField("Terrain", text);
    }

    @Override
    public void wpt(String latitude, String longitude) {
        cacheDetailsWriter.latitudeLongitude(latitude, longitude);
    }

    @Override
    public void wptDesc(String cacheName) {
    }

    @Override
    public void wptName(String wpt) throws IOException {
        cacheDetailsWriter.writeWptName();
    }

    @Override
    public void logText(String trimmedText) throws IOException {
        cacheDetailsWriter.writeLogText(trimmedText, encrypted);
    }

    @Override
    public void logType(String trimmedText) throws IOException {
        cacheDetailsWriter.logType(trimmedText);
    }

    @Override
    public void placedBy(String trimmedText) throws IOException {
        cacheDetailsWriter.placedBy(trimmedText);
    }

    @Override
    public void wptTime(String trimmedText) throws IOException {
        cacheDetailsWriter.wptTime(trimmedText);
    }

    @Override
    public void shortDescription(String trimmedText) throws IOException {
        cacheDetailsWriter.writeShortDescription(trimmedText);
    }

    @Override
    public void longDescription(String trimmedText) throws IOException {
        cacheDetailsWriter.writeLongDescription(trimmedText);
    }

    @Override
    public void setEncrypted(boolean mLogEncrypted) {
        encrypted = mLogEncrypted;
    }

    @Override
    public void logFinder(String text) {
        cacheDetailsWriter.writeLogFinder(text);
    }
}
