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

import com.google.code.geobeagle.cachedetails.DetailsDatabaseWriter;
import com.google.inject.Inject;

class TagWriter {
    private static final String SPACES = "                        ";
    private int mLevel;
    private final DetailsDatabaseWriter writer;
    private final StringBuffer stringBuffer;
    private String wpt;

    @Inject
    public TagWriter(DetailsDatabaseWriter writer) {
        this.writer = writer;
        stringBuffer = new StringBuffer();
    }

    public void close() {
        writer.write(wpt, stringBuffer.toString());
        wpt = null;
    }

    public void endTag(String name) {
        mLevel--;
        stringBuffer.append("</" + name + ">");
    }

    public void open(String wpt) {
        mLevel = 0;
        this.wpt = wpt;
        stringBuffer.setLength(0);
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    }

    public void startTag(Tag tag) {
        stringBuffer.append("\n" + SPACES.substring(0, Math.min(mLevel, SPACES.length())));
        mLevel++;
        stringBuffer.append("<" + tag.name);
        for (String key : tag.attributes.keySet()) {
            stringBuffer.append(" " + key + "='" + tag.attributes.get(key) + "'");
        }
        stringBuffer.append(">");

    }

    public void text(String text) {
        stringBuffer.append(text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
    }

    public void start() {
        writer.start();
    }

    public void end() {
        writer.end();
    }
}
