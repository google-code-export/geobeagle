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

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.cachelist.CacheListActivity;
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.GeocacheFromPreferencesFactory;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Intent.class, ActivityRestorer.class, Log.class
})
public class ActivityRestorerTest extends GeoBeagleTest {
    @Test
    public void createCacheListIntent() throws Exception {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Activity parent = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        String cacheList = ActivityType.CACHE_LIST.name();
        EasyMock.expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(cacheList);
        PowerMock.expectNew(Intent.class, parent, CacheListActivity.class).andReturn(intent);
        parent.startActivity(intent);
        parent.finish();

        PowerMock.replayAll();
        new ActivityRestorer(parent, null, sharedPreferences).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        PowerMock.verifyAll();
    }

    @Test
    public void createNull() throws Exception {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Activity parent = PowerMock.createMock(Activity.class);

        EasyMock.expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(ActivityType.NONE.name());

        PowerMock.replayAll();
        new ActivityRestorer(parent, null, sharedPreferences).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.CACHE_LIST);
        PowerMock.verifyAll();
    }

    @Test
    public void notNewTask() throws Exception {
        PowerMock.replayAll();
        new ActivityRestorer(null, null, null).restore(0, ActivityType.NONE);
        PowerMock.verifyAll();
    }

    @Test
    public void createSearchOnlineIntent() throws Exception {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Activity parent = PowerMock.createMock(Activity.class);

        String searchOnline = ActivityType.SEARCH_ONLINE.name();
        EasyMock.expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(searchOnline);

        PowerMock.replayAll();
        new ActivityRestorer(parent, null, sharedPreferences).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        PowerMock.verifyAll();
    }

    @Test
    public void activityTypeNone() throws Exception {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Activity parent = PowerMock.createMock(Activity.class);

        final String none = ActivityType.NONE.name();
        EasyMock.expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY,
                        ActivityType.NONE.toString())).andReturn(none);

        PowerMock.replayAll();
        new ActivityRestorer(parent, null, sharedPreferences).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        PowerMock.verifyAll();
    }

    @Test
    public void createViewCache() throws Exception {
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        Activity parent = PowerMock.createMock(Activity.class);
        GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = PowerMock
                .createMock(GeocacheFromPreferencesFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Intent intent = PowerMock.createMock(Intent.class);

        String viewCache = ActivityType.VIEW_CACHE.name();
        EasyMock.expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(viewCache);
        EasyMock.expect(geocacheFromPreferencesFactory.create(sharedPreferences)).andReturn(
                geocache);
        PowerMock.expectNew(Intent.class, parent, CompassActivity.class).andReturn(intent);
        EasyMock.expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        EasyMock.expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        parent.startActivity(intent);

        PowerMock.replayAll();
        new ActivityRestorer(parent, geocacheFromPreferencesFactory, sharedPreferences).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        PowerMock.verifyAll();
    }
}
