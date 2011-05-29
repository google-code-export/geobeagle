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

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class SmsLogger implements ICacheLogger {
    private final Context context;
    private final ErrorDisplayer errorDisplayer;
    private final FieldnoteStringsFVsDnf fieldNoteStringsFoundVsDnf;

    @Inject
    public SmsLogger(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf,
            Context context,
            ErrorDisplayer errorDisplayer) {
        this.fieldNoteStringsFoundVsDnf = fieldnoteStringsFVsDnf;
        this.context = context;
        this.errorDisplayer = errorDisplayer;
    }

    @Override
    public void log(CharSequence geocacheId, CharSequence logText, boolean dnf) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("address", "41411");
        sendIntent.putExtra("sms_body",
                fieldNoteStringsFoundVsDnf.getString(R.array.fieldnote_code, dnf) + geocacheId
                        + " " + logText);
        sendIntent.setType("vnd.android-dir/mms-sms");
        try {
            context.startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            errorDisplayer.displayError(R.string.sms_fail);
        }
    }
}
