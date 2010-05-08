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

import android.text.util.Linkify;
import android.widget.EditText;
import android.widget.TextView;

public class DialogHelperCommon {
    private final boolean mDnf;
    private final EditText mEditText;
    private final TextView mFieldnoteCaveat;
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;

    public DialogHelperCommon(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf, EditText editText,
            boolean dnf, TextView fieldnoteCaveat) {
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
        mDnf = dnf;
        mEditText = editText;
        mFieldnoteCaveat = fieldnoteCaveat;
    }

    public void configureDialogText() {
        Linkify.addLinks(mFieldnoteCaveat, Linkify.WEB_URLS);
    }

    public void configureEditor(String localDate) {
        final String defaultMessage = mFieldnoteStringsFVsDnf.getString(R.array.default_msg, mDnf);
        final String msg = String.format("(%1$s/%2$s) %3$s", localDate, mFieldnoteStringsFVsDnf
                .getString(R.array.geobeagle_sig, mDnf), defaultMessage);
        mEditText.setText(msg);
        final int len = msg.length();
        mEditText.setSelection(len - defaultMessage.length(), len);
    }
}
