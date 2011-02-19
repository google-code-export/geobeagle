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
    // private static final String SPACES = "                        ";
    private static final String SPACES = "....................";
    private int mLevel;
    private final DetailsDatabaseWriter writer;

    @Inject
    public TagWriter(DetailsDatabaseWriter writer) {
        this.writer = writer;
    }

    public void close() {
        writer.close();
    }

    public void endTag(String name) {
        mLevel--;
        if (writer != null) {
            System.err.println("</" + name + ">");
            writer.write("</" + name + ">");
        }
    }

    public void open(String wpt) {
        mLevel = 0;
        writer.open(wpt);
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    }

    public void startTag(Tag tag) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n" + SPACES.substring(0, Math.min(mLevel, SPACES.length())));
        mLevel++;
        sb.append("<" + tag.name);
        for (String key : tag.attributes.keySet()) {
            sb.append(" " + key + "='" + tag.attributes.get(key) + "'");
        }
        sb.append(">");
        System.err.println(sb.toString());

        writer.write(sb.toString());
    }

    public void text(String text) {
        System.err.println(text);

        writer.write(text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
    }

    public boolean isOpen() {
        return writer.isOpen();
    }
}
