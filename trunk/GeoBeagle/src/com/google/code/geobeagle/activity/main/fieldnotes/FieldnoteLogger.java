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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class FieldnoteLogger {
    // TODO: share one onClickCancel across app.
    public static class OnClickCancel implements OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    }

    public static class OnClickOk implements OnClickListener {
        private final CacheLogger mCacheLogger;
        private final boolean mDnf;
        private final EditText mEditText;
        private final CharSequence mGeocacheId;

        public OnClickOk(CharSequence geocacheId, EditText editText, CacheLogger cacheLogger,
                boolean dnf) {
            mGeocacheId = geocacheId;
            mEditText = editText;
            mCacheLogger = cacheLogger;
            mDnf = dnf;
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            mCacheLogger.log(mGeocacheId, mEditText.getText(), mDnf);
        }
    }

    static final String FIELDNOTES_FILE = "/sdcard/GeoBeagleFieldNotes.txt";
    private final DialogHelperCommon mDialogHelperCommon;
    private final DialogHelperFile mDialogHelperFile;
    private final DialogHelperSms mDialogHelperSms;

    public FieldnoteLogger(DialogHelperCommon dialogHelperCommon,
            DialogHelperFile dialogHelperFile, DialogHelperSms dialogHelperSms) {
        mDialogHelperSms = dialogHelperSms;
        mDialogHelperFile = dialogHelperFile;
        mDialogHelperCommon = dialogHelperCommon;
    }

    public void onPrepareDialog(Dialog dialog, SharedPreferences defaultSharedPreferences,
            String localDate) {
        final boolean fieldNoteTextFile = defaultSharedPreferences.getBoolean(
                "field-note-text-file", false);
        DialogHelper dialogHelper = fieldNoteTextFile ? mDialogHelperFile : mDialogHelperSms;

        dialogHelper.configureDialogText(dialog);
        mDialogHelperCommon.configureDialogText();

        dialogHelper.configureEditor();
        mDialogHelperCommon.configureEditor(localDate);
    }
}
