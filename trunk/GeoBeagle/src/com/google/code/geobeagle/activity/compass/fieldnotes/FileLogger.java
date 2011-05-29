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

package com.google.code.geobeagle.activity.compass.fieldnotes;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;
import com.google.inject.Inject;

import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Writes lines formatted like:
 *
 * <pre>
 * GC1FX1A,2008-09-27T21:04Z, Found it,&quot;log text&quot;
 * </pre>
 */

public class FileLogger implements ICacheLogger {
    private final Toaster mToaster;
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;
    private final DateFormatter mSimpleDateFormat;
    private final GeoBeagleEnvironment mGeoBeagleEnvironment;

    @Inject
    public FileLogger(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf,
            DateFormatter simpleDateFormat, Toaster toaster,
            GeoBeagleEnvironment geoBeagleEnvironment) {
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        mSimpleDateFormat = simpleDateFormat;
        mToaster = toaster;
        mGeoBeagleEnvironment = geoBeagleEnvironment;
    }

    @Override
    public void log(CharSequence geocacheId, CharSequence logText, boolean dnf) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
                    mGeoBeagleEnvironment.getFieldNotesFilename(), true), "UTF-16");
            final Date date = new Date();
            final String formattedDate = mSimpleDateFormat.format(date);
            final String logLine = String.format("%1$s,%2$s,%3$s,\"%4$s\"\n", geocacheId,
                    formattedDate, mFieldnoteStringsFVsDnf.getString(R.array.fieldnote_file_code,
                            dnf), logText.toString());
            writer.write(logLine);
            writer.close();
        } catch (IOException e) {
            mToaster.toast(R.string.error_writing_cache_log, Toast.LENGTH_LONG);
        }
    }
}
