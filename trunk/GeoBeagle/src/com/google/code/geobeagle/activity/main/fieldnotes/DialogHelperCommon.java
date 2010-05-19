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

import android.text.util.Linkify;
import android.widget.EditText;
import android.widget.TextView;

public class DialogHelperCommon {
    private final FieldnoteStringsFVsDnf mFieldnoteStringsFVsDnf;

    @Inject
    public DialogHelperCommon(FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf) {
        mFieldnoteStringsFVsDnf = fieldnoteStringsFVsDnf;
    }

    public void configureDialogText(TextView fieldNoteCaveat) {
        Linkify.addLinks(fieldNoteCaveat, Linkify.WEB_URLS);
    }

    public void configureEditor(EditText editText, String localDate, boolean dnf) {
        final String defaultMessage = mFieldnoteStringsFVsDnf.getString(R.array.default_msg, dnf);
        final String msg = String.format("(%1$s/%2$s) %3$s", localDate, mFieldnoteStringsFVsDnf
                .getString(R.array.geobeagle_sig, dnf), defaultMessage);
        editText.setText(msg);
        final int len = msg.length();
        editText.setSelection(len - defaultMessage.length(), len);
    }
}
