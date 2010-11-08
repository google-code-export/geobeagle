/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */
package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.cachedetails.CacheDetailsHtmlWriter;
import com.google.inject.Inject;

import java.io.IOException;

public class CacheXmlTagsToDetails extends CacheXmlTagHandler {

    private final CacheDetailsHtmlWriter cacheDetailsHtmlWriter;
    private boolean encrypted;

    @Inject
    public CacheXmlTagsToDetails(CacheDetailsHtmlWriter cacheDetailsHtmlWriter) {
        this.cacheDetailsHtmlWriter = cacheDetailsHtmlWriter;
    }

    @Override
    public void cacheType(String text) throws IOException {
        cacheDetailsHtmlWriter.writeField("Type", text);
    }

    @Override
    public void container(String text) throws IOException {
        cacheDetailsHtmlWriter.writeField("Container", text);
    }

    @Override
    public void difficulty(String text) throws IOException {
        cacheDetailsHtmlWriter.writeField("Difficulty", text);
    }

    @Override
    public void endCache(Source source) throws IOException {
        cacheDetailsHtmlWriter.close();
    }

    @Override
    public void groundspeakName(String text) throws IOException {
        cacheDetailsHtmlWriter.writeName(text);
    }

    @Override
    public void hint(String text) throws IOException {
        cacheDetailsHtmlWriter.writeHint(text);
    }

    @Override
    public void line(String text) throws IOException {
        cacheDetailsHtmlWriter.writeLine(text);
    }

    @Override
    public void logDate(String text) throws IOException {
        cacheDetailsHtmlWriter.writeLogDate(text);
    }

    @Override
    public void terrain(String text) throws IOException {
        cacheDetailsHtmlWriter.writeField("Terrain", text);
    }

    @Override
    public void wpt(String latitude, String longitude) {
        cacheDetailsHtmlWriter.latitudeLongitude(latitude, longitude);
    }

    @Override
    public void wptName(String wpt) throws IOException {
        cacheDetailsHtmlWriter.writeWptName();
    }

    @Override
    public void logText(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.writeLogText(trimmedText, encrypted);
    }

    @Override
    public void logType(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.logType(trimmedText);
    }

    @Override
    public void placedBy(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.placedBy(trimmedText);
    }

    @Override
    public void wptTime(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.wptTime(trimmedText);
    }

    @Override
    public void shortDescription(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.writeShortDescription(trimmedText);
    }

    @Override
    public void longDescription(String trimmedText) throws IOException {
        cacheDetailsHtmlWriter.writeLongDescription(trimmedText);
    }

    @Override
    public void setEncrypted(boolean mLogEncrypted) {
        encrypted = mLogEncrypted;
    }

    @Override
    public void logFinder(String text) {
        cacheDetailsHtmlWriter.writeLogFinder(text);
    }

    @Override
    public void url(String text) {
    }
}
