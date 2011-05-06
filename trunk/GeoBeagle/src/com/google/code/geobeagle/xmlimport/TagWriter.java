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

import com.google.code.geobeagle.cachedetails.FileAndDatabaseWriter;
import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.cachedetails.Writer;
import com.google.inject.Inject;

import java.io.IOException;

class TagWriter {
    private static final String SPACES = "                        ";
    private int mLevel;
    private final Writer writer;
    private FilePathStrategy filePathStrategy;

    @Inject
    public TagWriter(FileAndDatabaseWriter writer, FilePathStrategy filePathStrategy) {
        this.writer = writer;
        this.filePathStrategy = filePathStrategy;
    }

    // For testing.
    public TagWriter(Writer writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        writer.close();
    }

    public void endTag(String name) throws IOException {
        mLevel--;
        if (writer != null)
            writer.write("</" + name + ">");
    }

    public boolean isOpen() {
        return writer.isOpen();
    }

    public void open(String gpxName, String wpt, String type) throws IOException {
        String path = filePathStrategy.getPath(gpxName, wpt, type);
        mLevel = 0;
        writer.mkdirs(path);
        writer.open(path, wpt);
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    }

    public void startTag(Tag tag) throws IOException {
        writeNewline();
        mLevel++;
        writer.write("<" + tag.name);
        for (String key : tag.attributes.keySet()) {
            writer.write(" " + key + "='" + tag.attributes.get(key) + "'");
        }
        writer.write(">");
    }

    private void writeNewline() throws IOException {
        writer.write("\n" + SPACES.substring(0, Math.min(mLevel, SPACES.length())));
    }

    public void text(String text) throws IOException {
        writer.write(text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
    }
}
