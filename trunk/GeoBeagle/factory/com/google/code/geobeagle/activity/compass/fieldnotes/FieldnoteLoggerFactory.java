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

import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperCommon;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperFile;
import com.google.code.geobeagle.activity.compass.fieldnotes.DialogHelperSms;
import com.google.code.geobeagle.activity.compass.fieldnotes.FieldnoteLogger;
import com.google.inject.Inject;

import android.content.SharedPreferences;

public class FieldnoteLoggerFactory {

    private final DialogHelperCommon dialogHelperCommon;
    private final DialogHelperFile dialogHelperFile;
    private final SharedPreferences sharedPreferences;

    @Inject
    public FieldnoteLoggerFactory(DialogHelperCommon dialogHelperCommon,
            DialogHelperFile dialogHelperFile,
            SharedPreferences sharedPreferences) {
        this.dialogHelperCommon = dialogHelperCommon;
        this.dialogHelperFile = dialogHelperFile;
        this.sharedPreferences = sharedPreferences;
    }
    
    public FieldnoteLogger create(DialogHelperSms dialogHelperSms) {
        return new FieldnoteLogger(dialogHelperCommon, dialogHelperFile, dialogHelperSms,
                sharedPreferences);
    }

}