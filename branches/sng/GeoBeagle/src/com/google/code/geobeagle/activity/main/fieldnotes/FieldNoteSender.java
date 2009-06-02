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
import android.content.DialogInterface;
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

        EditText createEditor(View fieldNoteDialogView, CharSequence prefix, int dnfIndex,
                Resources mResources) {
            EditText editText = (EditText)fieldNoteDialogView.findViewById(R.id.fieldnote);
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            final String timestamp = "(" + dateFormat.format(new Date()) + "/"
                    + mResources.getStringArray(R.array.geobeagle_sig)[dnfIndex] + ") ";
            final String defaultMessage = mResources.getStringArray(R.array.default_msg)[dnfIndex];
            final String msg = timestamp + defaultMessage;
            editText.setText(msg);
            InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(160 - (mResources
                    .getStringArray(R.array.fieldnote_code)[dnfIndex] + prefix).length());
            editText.setFilters(new InputFilter[] {
                lengthFilter
            });
            int len = msg.length();
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
        private final SmsSender mSmsSender;
        private final Resources mResources;
        private final int mDnfIndex;

        public OnClickOk(Resources resources, int dnfIndex, SmsSender smsSender, EditText editText,
                CharSequence prefix) {
            mEditText = editText;
            mPrefix = prefix;
            mSmsSender = smsSender;
            mResources = resources;
            mDnfIndex = dnfIndex;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            mSmsSender.sendSMS("41411",
                    mResources.getStringArray(R.array.fieldnote_code)[mDnfIndex] + mPrefix
                            + mEditText.getText());
            dialog.dismiss();
        }
    }

    private final Builder mDialogBuilder;
    private final DialogHelper mDialogHelper;
    private final LayoutInflater mLayoutInflater;
    private final SmsSender mSmsSender;
    private final Resources mResources;

    FieldNoteSender(LayoutInflater layoutInflater, SmsSender smsSender,
            AlertDialog.Builder builder, DialogHelper dialogHelper, Resources resources) {
        mLayoutInflater = layoutInflater;
        mSmsSender = smsSender;
        mDialogBuilder = builder;
        mDialogHelper = dialogHelper;
        mResources = resources;
    }

    public Dialog createDialog(CharSequence geocacheId, int dnfIndex) {
        View fieldNoteDialogView = mLayoutInflater.inflate(R.layout.fieldnote, null);

        Linkify.addLinks((TextView)fieldNoteDialogView.findViewById(R.id.fieldnote_caveat),
                Linkify.WEB_URLS);
        CharSequence prefix = geocacheId + " ";
        EditText editText = mDialogHelper.createEditor(fieldNoteDialogView, prefix, dnfIndex,
                mResources);
        OnClickOk onClickOk = new OnClickOk(mResources, dnfIndex, mSmsSender, editText, prefix);
        OnClickCancel onClickCancel = new OnClickCancel();
        return mDialogHelper.createDialog(mDialogBuilder, fieldNoteDialogView, onClickOk,
                onClickCancel);
    }
}
