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


import com.google.inject.Inject;

import android.content.SharedPreferences;

public class CacheLogger implements ICacheLogger {
    private final FileLogger mFileLogger;
    private final SharedPreferences mSharedPreferences;
    private final SmsLogger mSmsLogger;
    private final DatabaseLogger mDatabaseLogger;

    @Inject
    public CacheLogger(SharedPreferences sharedPreferences,
            FileLogger fileLogger, SmsLogger smsLogger, DatabaseLogger databaseLogger) {
        mSharedPreferences = sharedPreferences;
        mFileLogger = fileLogger;
        mSmsLogger = smsLogger;
        mDatabaseLogger = databaseLogger;
    }

    @Override
    public void log(CharSequence geocacheId, CharSequence logText, boolean dnf) {
        final boolean fFieldNoteTextFile = mSharedPreferences.getBoolean(
                "field-note-text-file", false);
        mDatabaseLogger.log(geocacheId, dnf);
        if (fFieldNoteTextFile)
            mFileLogger.log(geocacheId, logText, dnf);
        else
            mSmsLogger.log(geocacheId, logText, dnf);
    }
}