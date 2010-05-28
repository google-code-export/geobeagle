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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class FieldNoteSender {

    static class DialogHelper {

        Dialog createDialog(Builder mDialogBuilder, View fieldNoteDialogView, OnClickOk onClickOk,
                OnClickCancel onClickCancel) {
            mDialogBuilder.setTitle(R.string.field_note_title);
            mDialogBuilder.setView(fieldNoteDialogView);
            mDialogBuilder.setPositiveButton(R.string.send_sms, onClickOk);
            mDialogBuilder.setNegativeButton(R.string.cancel, onClickCancel);
            return mDialogBuilder.create();
        }

        EditText createEditor(View fieldNoteDialogView, CharSequence prefix,
                FieldNoteResources fieldNoteResources) {
            final EditText editText = (EditText)fieldNoteDialogView.findViewById(R.id.fieldnote);
            final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            final String timestamp = "(" + dateFormat.format(new Date()) + "/"
                    + fieldNoteResources.getString(R.array.geobeagle_sig) + ") ";
            final String defaultMessage = fieldNoteResources.getString(R.array.default_msg);
            final String msg = timestamp + defaultMessage;
            editText.setText(msg);
            final InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(
                    160 - (fieldNoteResources.getString(R.array.fieldnote_code) + prefix).length());
            editText.setFilters(new InputFilter[] {
                lengthFilter
            });
            final int len = msg.length();
            editText.setSelection(len - defaultMessage.length(), len);
            return editText;
        }
    }

    static class OnClickCancel implements OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    }

    static class OnClickOk implements OnClickListener {
        private final EditText mEditText;
        private final CharSequence mPrefix;
        private final FieldNoteResources mFieldNoteResources;
        private final Context mContext;

        public OnClickOk(FieldNoteResources fieldNoteResources, EditText editText,
                CharSequence prefix, Context context) {
            mEditText = editText;
            mPrefix = prefix;
            mFieldNoteResources = fieldNoteResources;
            mContext = context;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("address", "41411");
            sendIntent.putExtra("sms_body",

            mFieldNoteResources.getString(R.array.fieldnote_code) + mPrefix + mEditText.getText());
            sendIntent.setType("vnd.android-dir/mms-sms");
            mContext.startActivity(sendIntent);
            dialog.dismiss();
        }
    }

    private final Builder mDialogBuilder;
    private final DialogHelper mDialogHelper;
    private final LayoutInflater mLayoutInflater;

    public static class FieldNoteResources {
        private final Resources mResources;
        private final boolean mDnf;

        public FieldNoteResources(Resources resources, int id) {
            mResources = resources;
            mDnf = (id == R.id.menu_log_dnf);
        }

        String getString(int id) {
            return mResources.getStringArray(id)[mDnf ? 0 : 1];
        }
    }

    FieldNoteSender(LayoutInflater layoutInflater, AlertDialog.Builder builder,
            DialogHelper dialogHelper) {
        mLayoutInflater = layoutInflater;
        mDialogBuilder = builder;
        mDialogHelper = dialogHelper;
    }

    public Dialog createDialog(CharSequence geocacheId, FieldNoteResources fieldNoteResources,
            Context context) {
        View fieldNoteDialogView = mLayoutInflater.inflate(R.layout.fieldnote, null);

        Linkify.addLinks((TextView)fieldNoteDialogView.findViewById(R.id.fieldnote_caveat),
                Linkify.WEB_URLS);
        CharSequence prefix = geocacheId + " ";
        EditText editText = mDialogHelper.createEditor(fieldNoteDialogView, prefix,
                fieldNoteResources);
        OnClickOk onClickOk = new OnClickOk(fieldNoteResources, editText, prefix, context);
        OnClickCancel onClickCancel = new OnClickCancel();
        return mDialogHelper.createDialog(mDialogBuilder, fieldNoteDialogView, onClickOk,
                onClickCancel);
    }
}
