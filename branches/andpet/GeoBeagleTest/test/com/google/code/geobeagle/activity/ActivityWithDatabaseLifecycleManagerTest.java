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

package com.google.code.geobeagle.activity;

import com.google.code.geobeagle.activity.ActivityWithDatabaseLifecycleManager;
import com.google.code.geobeagle.activity.PausableWithDatabase;
import com.google.code.geobeagle.database.NullClosable;
import com.google.code.geobeagle.database.DatabaseDI.GeoBeagleSqliteOpenHelper;
import com.google.code.geobeagle.database.DatabaseDI.SQLiteWrapper;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ActivityWithDatabaseLifecycleManagerTest {

    @Test
    public void testPauseAndOnResume() {
        final GeoBeagleSqliteOpenHelper geoBeagleSqliteOpenHelper = PowerMock
                .createMock(GeoBeagleSqliteOpenHelper.class);
        final SQLiteWrapper writableDatabase = PowerMock.createMock(SQLiteWrapper.class);
        final PausableWithDatabase pausableWithDatabase = PowerMock
                .createMock(PausableWithDatabase.class);
        final NullClosable nullClosable = PowerMock.createMock(NullClosable.class);
        EasyMock.expect(geoBeagleSqliteOpenHelper.getWritableSqliteWrapper()).andReturn(
                writableDatabase);
        pausableWithDatabase.onResume(writableDatabase);
        pausableWithDatabase.onPause();
        writableDatabase.close();

        // pausable should be idempotent.
        pausableWithDatabase.onPause();

        // We know close() is not idempotent though, so close a nullClosable
        // instead.
        nullClosable.close();

        PowerMock.replayAll();
        final ActivityWithDatabaseLifecycleManager activityWithDatabaseLifecycleManager = new ActivityWithDatabaseLifecycleManager(
                pausableWithDatabase, nullClosable, geoBeagleSqliteOpenHelper);
        activityWithDatabaseLifecycleManager.onResume();
        activityWithDatabaseLifecycleManager.onPause();
        activityWithDatabaseLifecycleManager.onPause();
        PowerMock.verifyAll();
    }
}
