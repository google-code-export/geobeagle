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

public class GpxWriter {
    public static class GpxWriterFactory {
        public GpxWriter create(CacheDetailsWriter cacheDetailsWriter) {
            return new GpxWriter(cacheDetailsWriter);
        }
    }

    final CacheDetailsWriter mCacheDetailsWriter;

    public GpxWriter(CacheDetailsWriter cacheDetailsWriter) {
        mCacheDetailsWriter = cacheDetailsWriter;
    }

    void writeEndTag() throws IOException {
        mCacheDetailsWriter.writeFooter();
        mCacheDetailsWriter.close();
    }

    void writeLine(String text) throws IOException {
        mCacheDetailsWriter.write(text);
    }

    void writeLogDate(String text) throws IOException {
        mCacheDetailsWriter.writeSeparator();
        mCacheDetailsWriter.write(text);
    }

    void writeWptName(String text, double latitude, double longitude) throws IOException {
        mCacheDetailsWriter.writeHeader();
        mCacheDetailsWriter.write(text);
        mCacheDetailsWriter.write(latitude + ", " + longitude);
    }
}
