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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.main.DateFormatter;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickCancel;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.Toaster;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Activity.class, FileLogger.class, DateFormat.class, Intent.class, Linkify.class,
        View.class, FieldnoteLogger.class, EditText.class, SimpleDateFormat.class,
        FileLogger.class, Format.class, DialogHelperCommon.class, LengthFilter.class,
        DialogHelperSms.class, SmsLogger.class
})
public class FieldNoteSenderTest {
    @Test
    public void testCacheLoggerFile() {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        FileLogger fileLogger = PowerMock.createMock(FileLogger.class);
        SmsLogger smsLogger = PowerMock.createMock(SmsLogger.class);

        EasyMock.expect(sharedPreferences.getBoolean("field-note-text-file", false))
                .andReturn(true);
        fileLogger.log("GC123", "easy find", false);

        PowerMock.replayAll();
        new CacheLogger(sharedPreferences, fileLogger, smsLogger).log("GC123", "easy find", false);
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheLoggerSms() {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        FileLogger fileLogger = PowerMock.createMock(FileLogger.class);
        SmsLogger smsLogger = PowerMock.createMock(SmsLogger.class);

        EasyMock.expect(sharedPreferences.getBoolean("field-note-text-file", false)).andReturn(
                false);
        smsLogger.log("GC123", "easy find", false);

        PowerMock.replayAll();
        new CacheLogger(sharedPreferences, fileLogger, smsLogger).log("GC123", "easy find", false);
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperCommonConfigureEditor() throws Exception {
        EditText editText = PowerMock.createMock(EditText.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = PowerMock
                .createMock(FieldnoteStringsFVsDnf.class);

        EasyMock.expect(fieldnoteStringsFVsDnf.getString(R.array.default_msg, false)).andReturn(
                "TFTC! ");
        EasyMock.expect(fieldnoteStringsFVsDnf.getString(R.array.geobeagle_sig, false)).andReturn(
                "fwgb");
        editText.setText("(12:36 PM/fwgb) TFTC! ");
        editText.setSelection(16, 22);
        PowerMock.replayAll();
        new DialogHelperCommon(fieldnoteStringsFVsDnf, editText, false, editText)
                .configureEditor("12:36 PM");
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperCommonConfigureText() {
        TextView fieldNoteCaveat = PowerMock.createMock(TextView.class);

        PowerMock.mockStatic(Linkify.class);
        EasyMock.expect(Linkify.addLinks(fieldNoteCaveat, Linkify.WEB_URLS)).andReturn(true);

        PowerMock.replayAll();
        new DialogHelperCommon(null, null, false, fieldNoteCaveat).configureDialogText();
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperFileConfigureEditor() {
        PowerMock.replayAll();
        new DialogHelperFile(null, null).configureEditor();
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperFileConfigureText() {
        Dialog dialog = PowerMock.createMock(Dialog.class);
        TextView fieldNoteCaveat = PowerMock.createMock(TextView.class);
        Context context = PowerMock.createMock(Context.class);

        EasyMock.expect(context.getString(R.string.field_note_file_caveat)).andReturn(
                ("file logging: %1$s"));
        fieldNoteCaveat.setText("file logging: " + FieldnoteLogger.FIELDNOTES_FILE);
        dialog.setTitle(R.string.log_cache_to_file);

        PowerMock.replayAll();
        new DialogHelperFile(fieldNoteCaveat, context).configureDialogText(dialog);
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperSmsConfigureEditor() throws Exception {
        EditText editText = PowerMock.createMock(EditText.class);
        InputFilter.LengthFilter lengthFilter = PowerMock
                .createMock(InputFilter.LengthFilter.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = PowerMock
                .createMock(FieldnoteStringsFVsDnf.class);

        EasyMock.expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_code, false)).andReturn(
                "GEOC @");
        PowerMock.expectNew(InputFilter.LengthFilter.class, 148).andReturn(lengthFilter);
        editText.setFilters((InputFilter[])EasyMock.anyObject());

        PowerMock.replayAll();
        new DialogHelperSms(5, fieldnoteStringsFVsDnf, editText, false, editText).configureEditor();
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperSmsConfigureText() {
        Dialog dialog = PowerMock.createMock(Dialog.class);
        TextView fieldNoteCaveat = PowerMock.createMock(TextView.class);

        fieldNoteCaveat.setText(R.string.sms_caveat);
        dialog.setTitle(R.string.log_cache_with_sms);

        PowerMock.replayAll();
        new DialogHelperSms(0, null, null, false, fieldNoteCaveat).configureDialogText(dialog);
        PowerMock.verifyAll();
    }

    @Test
    public void testFieldNoteResources() {
        Resources resources = PowerMock.createMock(Resources.class);

        String[] strings = new String[] {
                "dnf", "find"
        };
        EasyMock.expect(resources.getStringArray(17)).andReturn(strings).times(2);

        PowerMock.replayAll();
        assertEquals("find", new FieldnoteStringsFVsDnf(resources).getString(17, false));
        assertEquals("dnf", new FieldnoteStringsFVsDnf(resources).getString(17, true));
        PowerMock.verifyAll();

    }

    @Test
    public void testFieldNoteSenderOnPrepareDialogFile() {
        DialogHelperCommon dialogHelperCommon = PowerMock.createMock(DialogHelperCommon.class);
        DialogHelperFile dialogHelperFile = PowerMock.createMock(DialogHelperFile.class);
        Dialog dialog = PowerMock.createMock(Dialog.class);
        SharedPreferences defaultSharedPreferences = PowerMock.createMock(SharedPreferences.class);

        EasyMock.expect(defaultSharedPreferences.getBoolean("field-note-text-file", false))
                .andReturn(true);
        dialogHelperCommon.configureDialogText();
        dialogHelperFile.configureDialogText(dialog);
        dialogHelperCommon.configureEditor(null);
        dialogHelperFile.configureEditor();

        PowerMock.replayAll();
        new FieldnoteLogger(dialogHelperCommon, dialogHelperFile, null).onPrepareDialog(dialog,
                defaultSharedPreferences, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testFieldNoteSenderOnPrepareDialogSms() {
        DialogHelperCommon dialogHelperCommon = PowerMock.createMock(DialogHelperCommon.class);
        DialogHelperSms dialogHelperSms = PowerMock.createMock(DialogHelperSms.class);
        Dialog dialog = PowerMock.createMock(Dialog.class);
        SharedPreferences defaultSharedPreferences = PowerMock.createMock(SharedPreferences.class);

        EasyMock.expect(defaultSharedPreferences.getBoolean("field-note-text-file", false))
                .andReturn(false);
        dialogHelperCommon.configureDialogText();
        dialogHelperSms.configureDialogText(dialog);
        dialogHelperCommon.configureEditor(null);
        dialogHelperSms.configureEditor();

        PowerMock.replayAll();
        new FieldnoteLogger(dialogHelperCommon, null, dialogHelperSms).onPrepareDialog(dialog,
                defaultSharedPreferences, null);
        PowerMock.verifyAll();
    }

    @Test
    public void testFileLogger() throws Exception {
        Date date = PowerMock.createMock(Date.class);
        DateFormatter dateFormat = PowerMock.createMock(DateFormatter.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = PowerMock
                .createMock(FieldnoteStringsFVsDnf.class);
        OutputStreamWriter outputStreamWriter = PowerMock.createMock(OutputStreamWriter.class);
        FileOutputStream fileOutputStream = PowerMock.createMock(FileOutputStream.class);

        PowerMock.expectNew(FileOutputStream.class, FieldnoteLogger.FIELDNOTES_FILE, true)
                .andReturn(fileOutputStream);
        PowerMock.expectNew(OutputStreamWriter.class, fileOutputStream, "UTF-16").andReturn(
                outputStreamWriter);

        PowerMock.mockStatic(DateFormat.class);
        PowerMock.expectNew(Date.class).andReturn(date);
        EasyMock.expect(dateFormat.format(date)).andReturn("2008-09-27T21:04Z");

        EasyMock.expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_file_code, false))
                .andReturn("Found it");
        outputStreamWriter.write("GC123,2008-09-27T21:04Z,Found it,\"easy find\"\n");
        outputStreamWriter.close();

        PowerMock.replayAll();
        new FileLogger(fieldnoteStringsFVsDnf, dateFormat, null).log("GC123", "easy find", false);
        PowerMock.verifyAll();
    }

    @Test
    public void testFileLoggerError() throws Exception {
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = PowerMock
                .createMock(FieldnoteStringsFVsDnf.class);
        Toaster toaster = PowerMock.createMock(Toaster.class);
        IOException exception = new IOException();

        PowerMock.expectNew(FileOutputStream.class, FieldnoteLogger.FIELDNOTES_FILE, true)
                .andThrow(exception);
        toaster.showToast();

        PowerMock.replayAll();
        new FileLogger(fieldnoteStringsFVsDnf, null, toaster).log("GC123", "easy find", false);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClickCancel() {
        Dialog dialog = PowerMock.createMock(Dialog.class);
        dialog.dismiss();

        PowerMock.replayAll();
        new OnClickCancel().onClick(dialog, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClickOk() {
        EditText editText = PowerMock.createMock(EditText.class);
        CacheLogger cacheLogger = PowerMock.createMock(CacheLogger.class);
        Editable e = PowerMock.createMock(Editable.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(editText.getText()).andReturn(e);
        cacheLogger.log("GC123", e, false);
        dbFrontend.setGeocacheTag("GC123", Tags.DNF, false);
        dbFrontend.setGeocacheTag("GC123", Tags.FOUND, true);
        
        EasyMock.expect(editText.getText()).andReturn(e);
        cacheLogger.log("GC123", e, true);
        dbFrontend.setGeocacheTag("GC123", Tags.DNF, true);
        dbFrontend.setGeocacheTag("GC123", Tags.FOUND, false);
        PowerMock.replayAll();

        new OnClickOk("GC123", editText, cacheLogger, dbFrontend, false).onClick(null, 0);
        new OnClickOk("GC123", editText, cacheLogger, dbFrontend, true).onClick(null, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testSmsLogger() throws Exception {
        Intent intent = PowerMock.createMock(Intent.class);
        Context context = PowerMock.createMock(Context.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = PowerMock
                .createMock(FieldnoteStringsFVsDnf.class);

        EasyMock.expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_code, false)).andReturn(
                "GEOC @");
        PowerMock.expectNew(Intent.class, Intent.ACTION_VIEW).andReturn(intent);
        EasyMock.expect(intent.putExtra("address", "41411")).andReturn(intent);
        EasyMock
                .expect(intent.putExtra((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(intent);
        EasyMock.expect(intent.setType("vnd.android-dir/mms-sms")).andReturn(intent);
        context.startActivity(intent);

        PowerMock.replayAll();
        new SmsLogger(fieldnoteStringsFVsDnf, context).log("GC123", "easy find", false);
        PowerMock.verifyAll();
    }
}
