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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.anyObject;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.fieldnotes.CacheLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.DateFormatter;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperCommon;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperFile;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperSms;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteStringsFVsDnf;
import com.google.code.geobeagle.activity.compass.fieldnotes.FileLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.SmsLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.Toaster;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.xmlimport.GeoBeagleEnvironment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
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
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Activity.class, FileLogger.class, DateFormat.class, Intent.class, Linkify.class,
        View.class, FieldnoteLogger.class, EditText.class, SimpleDateFormat.class,
        FileLogger.class, Format.class, DialogHelperCommon.class, LengthFilter.class,
        DialogHelperSms.class, SmsLogger.class
})
public class FieldNoteSenderTest {
    private GeoBeagleEnvironment geoBeagleEnvironment;

    @Before
    public void setUp() {
        geoBeagleEnvironment = createMock(GeoBeagleEnvironment.class);
    }

    @Test
    public void testDialogHelperCommonConfigureEditor() throws Exception {
        EditText editText = createMock(EditText.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = createMock(FieldnoteStringsFVsDnf.class);

        expect(fieldnoteStringsFVsDnf.getString(R.array.default_msg, false)).andReturn("TFTC! ");
        expect(fieldnoteStringsFVsDnf.getString(R.array.geobeagle_sig, false)).andReturn("fwgb");
        editText.setText("(12:36 PM/fwgb) TFTC! ");
        editText.setSelection(16, 22);
        replayAll();
        new DialogHelperCommon(fieldnoteStringsFVsDnf).configureEditor(editText, "12:36 PM", false);
        verifyAll();
    }

    @Test
    public void testDialogHelperCommonConfigureText() {
        TextView fieldNoteCaveat = createMock(TextView.class);

        mockStatic(Linkify.class);
        expect(Linkify.addLinks(fieldNoteCaveat, Linkify.WEB_URLS)).andReturn(true);

        replayAll();
        new DialogHelperCommon(null).configureDialogText(fieldNoteCaveat);
        verifyAll();
    }

    @Test
    public void testDialogHelperFileConfigureEditor() {
        replayAll();
        new DialogHelperFile(null, null).configureEditor(null);
        verifyAll();
    }

    @Test
    public void testDialogHelperFileConfigureText() {
        Dialog dialog = createMock(Dialog.class);
        TextView fieldNoteCaveat = createMock(TextView.class);
        Context context = createMock(Context.class);

        expect(geoBeagleEnvironment.getFieldNotesFilename()).andReturn("fieldnotes.txt");
        expect(context.getString(R.string.field_note_file_caveat))
                .andReturn(("file logging: %1$s"));
        fieldNoteCaveat.setText("file logging: " + "fieldnotes.txt");
        dialog.setTitle(R.string.log_cache_to_file);

        replayAll();
        new DialogHelperFile(context, geoBeagleEnvironment).configureDialogText(dialog,
                fieldNoteCaveat);
        verifyAll();
    }

    @Test
    public void testDialogHelperSmsConfigureEditor() throws Exception {
        EditText editText = createMock(EditText.class);
        InputFilter.LengthFilter lengthFilter = createMock(InputFilter.LengthFilter.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = createMock(FieldnoteStringsFVsDnf.class);

        expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_code, false)).andReturn("GEOC @");
        expectNew(InputFilter.LengthFilter.class, 148).andReturn(lengthFilter);
        editText.setFilters((InputFilter[])anyObject());

        replayAll();
        new DialogHelperSms(fieldnoteStringsFVsDnf, 5, false).configureEditor(editText);
        verifyAll();
    }

    @Test
    public void testDialogHelperSmsConfigureText() {
        Dialog dialog = createMock(Dialog.class);
        TextView fieldNoteCaveat = createMock(TextView.class);

        fieldNoteCaveat.setText(R.string.sms_caveat);
        dialog.setTitle(R.string.log_cache_with_sms);

        replayAll();
        new DialogHelperSms(null, 0, false).configureDialogText(dialog, fieldNoteCaveat);
        verifyAll();
    }

    @Test
    public void testFieldNoteResources() {
        Resources resources = createMock(Resources.class);

        String[] strings = new String[] {
                "dnf", "find"
        };
        expect(resources.getStringArray(17)).andReturn(strings).times(2);

        replayAll();
        assertEquals("find", new FieldnoteStringsFVsDnf(resources).getString(17, false));
        assertEquals("dnf", new FieldnoteStringsFVsDnf(resources).getString(17, true));
        verifyAll();

    }

    @Test
    public void testFieldNoteSenderOnPrepareDialogFile() {
        DialogHelperCommon dialogHelperCommon = createMock(DialogHelperCommon.class);
        DialogHelperFile dialogHelperFile = createMock(DialogHelperFile.class);
        Dialog dialog = createMock(Dialog.class);
        EditText editText = createMock(EditText.class);
        TextView fieldnoteCaveat = createMock(TextView.class);
        SharedPreferences defaultSharedPreferences = createMock(SharedPreferences.class);

        expect(defaultSharedPreferences.getBoolean("field-note-text-file", false)).andReturn(true);
        expect(dialog.findViewById(R.id.fieldnote_caveat)).andReturn(fieldnoteCaveat);
        dialogHelperFile.configureDialogText(dialog, fieldnoteCaveat);
        dialogHelperCommon.configureEditor(editText, null, false);
        expect(dialog.findViewById(R.id.fieldnote)).andReturn(editText);

        dialogHelperFile.configureEditor(editText);
        dialogHelperCommon.configureDialogText(fieldnoteCaveat);

        replayAll();
        new FieldnoteLogger(dialogHelperCommon, dialogHelperFile, null, defaultSharedPreferences)
                .onPrepareDialog(dialog, null, false);
        verifyAll();
    }

    @Test
    public void testFieldNoteSenderOnPrepareDialogSms() {
        DialogHelperCommon dialogHelperCommon = createMock(DialogHelperCommon.class);
        DialogHelperSms dialogHelperSms = createMock(DialogHelperSms.class);
        Dialog dialog = createMock(Dialog.class);
        EditText editText = createMock(EditText.class);
        TextView fieldnoteCaveat = createMock(TextView.class);
        SharedPreferences defaultSharedPreferences = createMock(SharedPreferences.class);

        expect(defaultSharedPreferences.getBoolean("field-note-text-file", false)).andReturn(false);
        expect(dialog.findViewById(R.id.fieldnote_caveat)).andReturn(fieldnoteCaveat);
        dialogHelperSms.configureDialogText(dialog, fieldnoteCaveat);
        dialogHelperCommon.configureEditor(editText, null, false);
        expect(dialog.findViewById(R.id.fieldnote)).andReturn(editText);

        dialogHelperSms.configureEditor(editText);
        dialogHelperCommon.configureDialogText(fieldnoteCaveat);

        replayAll();
        new FieldnoteLogger(dialogHelperCommon, null, dialogHelperSms, defaultSharedPreferences)
                .onPrepareDialog(dialog, null, false);
        verifyAll();
    }

    @Test
    public void testFileLogger() throws Exception {
        Date date = createMock(Date.class);
        DateFormatter dateFormat = createMock(DateFormatter.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = createMock(FieldnoteStringsFVsDnf.class);
        OutputStreamWriter outputStreamWriter = createMock(OutputStreamWriter.class);
        FileOutputStream fileOutputStream = createMock(FileOutputStream.class);

        expect(geoBeagleEnvironment.getFieldNotesFilename()).andReturn("fieldnotes.log");
        expectNew(FileOutputStream.class, "fieldnotes.log", true).andReturn(fileOutputStream);
        expectNew(OutputStreamWriter.class, fileOutputStream, "UTF-16").andReturn(
                outputStreamWriter);

        mockStatic(DateFormat.class);
        expectNew(Date.class).andReturn(date);
        expect(dateFormat.format(date)).andReturn("2008-09-27T21:04Z");

        expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_file_code, false)).andReturn(
                "Found it");
        outputStreamWriter.write("GC123,2008-09-27T21:04Z,Found it,\"easy find\"\n");
        outputStreamWriter.close();

        replayAll();
        new FileLogger(fieldnoteStringsFVsDnf, dateFormat, null, geoBeagleEnvironment).log("GC123",
                "easy find", false);
        verifyAll();
    }

    @Test
    public void testFileLoggerError() throws Exception {
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = createMock(FieldnoteStringsFVsDnf.class);
        Toaster toaster = createMock(Toaster.class);
        IOException exception = new IOException();

        expect(geoBeagleEnvironment.getFieldNotesFilename()).andReturn("fieldnotes.log");
        expectNew(FileOutputStream.class, "fieldnotes.log", true).andThrow(exception);
        toaster.toast(R.string.error_writing_cache_log, Toast.LENGTH_LONG);

        replayAll();
        new FileLogger(fieldnoteStringsFVsDnf, null, toaster, geoBeagleEnvironment).log("GC123",
                "easy find", false);
        verifyAll();
    }

    @Test
    public void testOnClickOk() {
        EditText editText = createMock(EditText.class);
        CacheLogger cacheLogger = createMock(CacheLogger.class);
        Editable e = createMock(Editable.class);

        expect(editText.getText()).andReturn(e);
        cacheLogger.log("GC123", e, false);
        replayAll();
        new OnClickOk("GC123", editText, cacheLogger, false).onClick(null, 0);
        verifyAll();
    }

    @Test
    public void testSmsLogger() throws Exception {
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);
        FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = createMock(FieldnoteStringsFVsDnf.class);

        expect(fieldnoteStringsFVsDnf.getString(R.array.fieldnote_code, false)).andReturn("GEOC @");
        expectNew(Intent.class, Intent.ACTION_VIEW).andReturn(intent);
        expect(intent.putExtra("address", "41411")).andReturn(intent);
        expect(intent.putExtra((String)anyObject(), (String)anyObject())).andReturn(intent);
        expect(intent.setType("vnd.android-dir/mms-sms")).andReturn(intent);
        context.startActivity(intent);

        replayAll();
        new SmsLogger(fieldnoteStringsFVsDnf, context, null).log("GC123", "easy find", false);
        verifyAll();
    }
}
