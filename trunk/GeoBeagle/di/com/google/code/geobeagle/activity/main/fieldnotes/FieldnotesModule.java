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
import com.google.code.geobeagle.activity.main.fieldnotes.FileLogger.ToasterErrorWritingLog;
import com.google.code.geobeagle.xmlimport.GpxImporterDI.Toaster;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryProvider;

import roboguice.config.AbstractAndroidModule;

import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FieldnotesModule extends AbstractAndroidModule {

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
    @ToasterErrorWritingLog
    Toaster toasterProvider(GeoBeagle geoBeagle) {
        return new Toaster(geoBeagle, R.string.error_writing_cache_log, Toast.LENGTH_LONG);
    }

    @Provides
    CharSequence providesGeocacheId(GeoBeagle geoBeagle) {
        return geoBeagle.getGeocache().getId();
    }
}
