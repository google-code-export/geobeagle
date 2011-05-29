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

import com.google.code.geobeagle.activity.compass.fieldnotes.CacheLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.DatabaseLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.FileLogger;
import com.google.code.geobeagle.activity.compass.fieldnotes.SmsLogger;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
public class CacheLoggerTest {

    @Test
    public void testCacheLoggerFile() {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        FileLogger fileLogger = PowerMock.createMock(FileLogger.class);
        SmsLogger smsLogger = PowerMock.createMock(SmsLogger.class);
        DatabaseLogger databaseLogger = PowerMock.createMock(DatabaseLogger.class);

        EasyMock.expect(sharedPreferences.getBoolean("field-note-text-file", false))
                .andReturn(true);
        databaseLogger.log("GC123", false);
        fileLogger.log("GC123", "easy find", false);

        PowerMock.replayAll();
        new CacheLogger(sharedPreferences, fileLogger, smsLogger, databaseLogger).log("GC123",
                "easy find", false);
        PowerMock.verifyAll();
    }

    @Test
    public void testCacheLoggerSms() {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        FileLogger fileLogger = PowerMock.createMock(FileLogger.class);
        SmsLogger smsLogger = PowerMock.createMock(SmsLogger.class);
        DatabaseLogger databaseLogger = PowerMock.createMock(DatabaseLogger.class);

        EasyMock.expect(sharedPreferences.getBoolean("field-note-text-file", false)).andReturn(
                false);
        databaseLogger.log("GC123", false);
        smsLogger.log("GC123", "easy find", false);

        PowerMock.replayAll();
        new CacheLogger(sharedPreferences, fileLogger, smsLogger, databaseLogger).log("GC123",
                "easy find", false);
        PowerMock.verifyAll();
    }

}
