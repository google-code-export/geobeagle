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
import com.google.code.geobeagle.activity.main.GeoBeagle;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperSms.DialogHelperSmsFactory;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.FieldnoteLoggerFactory;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOk;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOkFactory;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.name.Named;

import roboguice.config.AbstractAndroidModule;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FieldnotesModule extends AbstractAndroidModule {
    private static View mView;

    @Override
    protected void configure() {
        bind(DateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"));
        bind(OnClickOkFactory.class).toProvider(
                FactoryProvider.newFactory(OnClickOkFactory.class, OnClickOk.class));
        bind(DialogHelperSmsFactory.class).toProvider(
                FactoryProvider.newFactory(DialogHelperSmsFactory.class, DialogHelperSms.class));
        bind(FieldnoteLoggerFactory.class).toProvider(
                FactoryProvider.newFactory(FieldnoteLoggerFactory.class, FieldnoteLogger.class));
    }

    @Provides
    Toaster toasterProvider(GeoBeagle geoBeagle) {
        return new Toaster(geoBeagle, R.string.error_writing_cache_log, Toast.LENGTH_LONG);
    }

    public static void resetView() {
        mView = null;
    }

    @Provides
    CharSequence providesGeocacheId(GeoBeagle geoBeagle) {
        return geoBeagle.getGeocache().getId();
    }

    @Provides
    @Named("FieldNoteDialogView")
    View providesFieldNoteDialogView(GeoBeagle geoBeagle, LayoutInflater layoutInflater) {
        if (mView == null) {
            mView = layoutInflater.inflate(R.layout.fieldnote, null);
        }
        Log.d("GeoBeagle", "provider gb, layoutInflater: " + geoBeagle + ", " + layoutInflater);
        return mView;
    }

    @Provides
    @Named("FieldNoteEditText")
    EditText providesFieldNoteEditText(@Named("FieldNoteDialogView") View view) {
        EditText editText = (EditText)view.findViewById(R.id.fieldnote);
        Log.d("GeoBeagle", "Getting edit text: " + editText);
        return editText;
    }

    @Provides
    @Named("FieldNoteCaveat")
    TextView providesFieldNoteCaveat(@Named("FieldNoteDialogView") View view) {
        Log.d("GeoBeagle", "Binding caveat: " + view);
        TextView textView = (TextView)view.findViewById(R.id.fieldnote_caveat);
        Log.d("GeoBeagle", "Getting textView: " + textView);
        return textView;
    }

}
