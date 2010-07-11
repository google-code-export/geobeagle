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

package com.google.code.geobeagle.activity.main.fieldnotes;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnotesModule.FieldNotesFilename;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Writes lines formatted like:
 * 
 * <pre>
 * GC1FX1A,2008-09-27T21:04Z, Found it,&quot;log text&quot;
 * </pre>
 */

public class FileLogger implements ICacheLogger {
    private final Toaster mErrorToaster;
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;
    private final DateFormatter mSimpleDateFormat;
    private final String mFieldNotesFile;
    
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface ToasterErrorWritingLog {}

    @Inject
    public FileLogger(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf,
            DateFormatter simpleDateFormat, @ToasterErrorWritingLog Toaster errorToaster,
            @FieldNotesFilename String fieldNotesFile) {
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        mSimpleDateFormat = simpleDateFormat;
        mErrorToaster = errorToaster;
        mFieldNotesFile = fieldNotesFile;
    }

    public void log(CharSequence geocacheId, CharSequence logText, boolean dnf) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
                    mFieldNotesFile, true), "UTF-16");
            final Date date = new Date();
            final String formattedDate = mSimpleDateFormat.format(date);
            final String logLine = String.format("%1$s,%2$s,%3$s,\"%4$s\"\n", geocacheId,
                    formattedDate, mFieldnoteStringsFVsDnf.getString(R.array.fieldnote_file_code,
                            dnf), logText.toString());
            writer.write(logLine);
            writer.close();
        } catch (IOException e) {
            mErrorToaster.showToast();
        }
    }
}
