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

package com.google.code.geobeagle.cachedetails;

import com.google.inject.Singleton;

import java.io.IOException;
import java.io.StringWriter;

//TODO: remove singleton.
@Singleton
public class StringWriterWrapper implements com.google.code.geobeagle.cachedetails.Writer,
        NullWriterOpener {

    private final StringWriter stringWriter;

    public StringWriterWrapper() {
        this.stringWriter = new StringWriter();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void open() throws IOException {
        stringWriter.getBuffer().setLength(0);
    }

    public String getString() {
        return stringWriter.toString();
    }

    @Override
    public void write(String str) throws IOException {
        // Log.d("GeoBeagle", ":: " + str);
        stringWriter.write(str);
    }

    @Override
    public void mkdirs(String path) {
    }
}
