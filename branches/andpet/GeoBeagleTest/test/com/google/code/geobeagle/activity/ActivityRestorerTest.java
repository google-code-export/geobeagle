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

import com.google.code.geobeagle.activity.ActivityRestorer.CacheListRestorer;
import com.google.code.geobeagle.activity.ActivityRestorer.NullRestorer;
import com.google.code.geobeagle.activity.ActivityRestorer.Restorer;
import com.google.code.geobeagle.activity.ActivityRestorer.ViewCacheRestorer;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.main.GeoBeagle;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Intent.class, ActivityRestorer.class
})
public class ActivityRestorerTest {
    @Test
    public void createCacheListIntent() throws Exception {
        Activity parent = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.expectNew(Intent.class, parent, CacheListActivity.class).andReturn(intent);
        parent.startActivity(intent);
        parent.finish();

        PowerMock.replayAll();
        new CacheListRestorer(parent).restore();
        PowerMock.verifyAll();
    }

    @Test
    public void NullRestorerTest() throws Exception {
        PowerMock.replayAll();
        new NullRestorer().restore();
        PowerMock.verifyAll();
    }

    @Test
    public void notNewTaskTest() throws Exception {
        PowerMock.replayAll();
        new ActivityRestorer(null, null, null).restore(0);
        PowerMock.verifyAll();
    }

    @Test
    public void viewCacheRestorerTest() throws Exception {
        Activity parent = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);

        EasyMock.expect(sharedPreferences.getString("geocacheId", "")).andReturn("gcabc");
        PowerMock.expectNew(Intent.class, parent, GeoBeagle.class).andReturn(intent);
        EasyMock.expect(intent.putExtra("geocacheId", "gcabc")).andReturn(intent);
        EasyMock.expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        parent.startActivity(intent);
        parent.finish();

        PowerMock.replayAll();
        new ViewCacheRestorer(sharedPreferences, parent).restore();
        PowerMock.verifyAll();
    }

    @Test
    public void activityRestorerTest() throws Exception {
        ActivityDI.ActivityTypeFactory activityTypeFactory = PowerMock
                .createMock(ActivityDI.ActivityTypeFactory.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Restorer restorer = PowerMock.createMock(Restorer.class);
        Restorer restorers[] = {
                null, null, null, restorer
        };

        final int viewCache = ActivityType.VIEW_CACHE.toInt();
        EasyMock.expect(
                sharedPreferences.getInt(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.toInt()))
                .andReturn(viewCache);
        EasyMock.expect(activityTypeFactory.fromInt(viewCache)).andReturn(ActivityType.VIEW_CACHE);
        restorer.restore();

        PowerMock.replayAll();
        new ActivityRestorer(activityTypeFactory, sharedPreferences, restorers)
                .restore(Intent.FLAG_ACTIVITY_NEW_TASK);
        PowerMock.verifyAll();
    }
}
