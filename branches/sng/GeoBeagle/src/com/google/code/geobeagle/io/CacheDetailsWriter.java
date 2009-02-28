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

package com.google.code.geobeagle.io;

import java.io.IOException;

public class CacheDetailsWriter {
    public static class CacheDetailsWriterFactory {
        public CacheDetailsWriter create(HtmlWriter htmlWriter) {
            return new CacheDetailsWriter(htmlWriter);
        }
    }

    final HtmlWriter mHtlmWriter;

    public CacheDetailsWriter(HtmlWriter htmlWriter) {
        mHtlmWriter = htmlWriter;
    }

    void writeEndTag() throws IOException {
        mHtlmWriter.writeFooter();
        mHtlmWriter.close();
    }

    void writeLine(String text) throws IOException {
        mHtlmWriter.write(text);
    }

    void writeLogDate(String text) throws IOException {
        mHtlmWriter.writeSeparator();
        mHtlmWriter.write(text);
    }

    void writeWptName(String text, double latitude, double longitude) throws IOException {
        mHtlmWriter.writeHeader();
        mHtlmWriter.write(text);
        mHtlmWriter.write(latitude + ", " + longitude);
    }
}
