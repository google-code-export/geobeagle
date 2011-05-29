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

package com.google.code.geobeagle.activity.cachelist;

import com.google.code.geobeagle.OnClickCancelListener;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperSms;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperSmsFactory;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLoggerFactory;
import com.google.code.geobeagle.activity.compass.fieldnotes.HasGeocache;
import com.google.code.geobeagle.activity.compass.fieldnotes.OnClickOkFactory;
import com.google.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;

public class LogFindDialogHelper {
    private static final DateFormat mLocalDateFormat = DateFormat
            .getTimeInstance(DateFormat.MEDIUM);

    private final DialogHelperSmsFactory dialogHelperSmsFactory;
    private final FieldnoteLoggerFactory fieldnoteLoggerFactory;
    private final OnClickOkFactory onClickOkFactory;
    private final OnClickCancelListener onClickCancelListener;
    private final HasGeocache hasGeocache;

    @Inject
    LogFindDialogHelper(DialogHelperSmsFactory dialogHelperSmsFactory,
            FieldnoteLoggerFactory fieldnoteLoggerFactory,
            OnClickOkFactory onClickOkFactory,
            OnClickCancelListener onClickCancelListener,
            HasGeocache hasGeocache) {
        this.dialogHelperSmsFactory = dialogHelperSmsFactory;
        this.fieldnoteLoggerFactory = fieldnoteLoggerFactory;
        this.onClickOkFactory = onClickOkFactory;
        this.onClickCancelListener = onClickCancelListener;
        this.hasGeocache = hasGeocache;
    }

    public void onPrepareDialog(Activity activity, int id, Dialog dialog) {
        CharSequence cacheId = hasGeocache.get(activity).getId();
        boolean fDnf = id == R.id.menu_log_dnf;
        DialogHelperSms dialogHelperSms = dialogHelperSmsFactory.create(cacheId.length(), fDnf);
        FieldnoteLogger fieldnoteLogger = fieldnoteLoggerFactory.create(dialogHelperSms);

        fieldnoteLogger.onPrepareDialog(dialog, mLocalDateFormat.format(new Date()), fDnf);
    }

    public Dialog onCreateDialog(Activity activity, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View fieldnoteDialogView = LayoutInflater.from(activity).inflate(R.layout.fieldnote, null);

        boolean fDnf = id == R.id.menu_log_dnf;

        OnClickOk onClickOk = onClickOkFactory.create(
                (EditText)fieldnoteDialogView.findViewById(R.id.fieldnote), fDnf);
        builder.setTitle(R.string.field_note_title);
        builder.setView(fieldnoteDialogView);
        builder.setNegativeButton(R.string.cancel, onClickCancelListener);
        builder.setPositiveButton(R.string.log_cache, onClickOk);
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

}
