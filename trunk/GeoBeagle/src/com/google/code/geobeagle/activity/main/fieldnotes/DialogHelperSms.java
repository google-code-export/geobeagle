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

import com.google.code.geobeagle.R;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.app.Dialog;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.widget.EditText;
import android.widget.TextView;

public class DialogHelperSms implements DialogHelper {
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;
    private final int mGeocacheIdLength;
    private final boolean mFDnf;

    public interface DialogHelperSmsFactory {
        public DialogHelperSms create(int geocacheIdLength, boolean fDnf);
    }

    @Inject
    public DialogHelperSms(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf,
            @Assisted int geocacheIdLength, @Assisted boolean fDnf) {
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        mGeocacheIdLength = geocacheIdLength;
        mFDnf = fDnf;
    }

    @Override
    public void configureEditor(Dialog dialog) {
        final LengthFilter lengthFilter = new LengthFilter(
                160 - (mGeocacheIdLength + 1 + mFieldnoteStringsFVsDnf.getString(
                        R.array.fieldnote_code, mFDnf).length()));
        ((EditText)dialog.findViewById(R.id.fieldnote)).setFilters(new InputFilter[] {
            lengthFilter
        });
    }

    @Override
    public void configureDialogText(Dialog dialog, TextView fieldnoteCaveat) {
        fieldnoteCaveat.setText(R.string.sms_caveat);
        dialog.setTitle(R.string.log_cache_with_sms);
    }
}
