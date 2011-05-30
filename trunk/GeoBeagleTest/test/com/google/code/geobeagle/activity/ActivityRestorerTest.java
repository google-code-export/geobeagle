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
import com.google.code.geobeagle.activity.cachelist.GeoBeagleTest;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.CompassActivity;
import com.google.code.geobeagle.activity.compass.GeocacheFromPreferencesFactory;

import static org.easymock.EasyMock.expect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Intent.class, ActivityRestorer.class, Log.class
})
public class ActivityRestorerTest extends GeoBeagleTest {
    private SharedPreferences sharedPreferences;
    private Activity parent;
    private CacheListRestorer cacheListRestorer;

    @Before
    public void setUp() {
        sharedPreferences = createMock(SharedPreferences.class);
        parent = createMock(Activity.class);
        cacheListRestorer = createMock(CacheListRestorer.class);
    }

    @Test
    public void createCacheListIntent() throws Exception {
        String cacheList = ActivityType.CACHE_LIST.name();
        expect(sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(cacheList);
        cacheListRestorer.restore();

        replayAll();
        new ActivityRestorer(parent, null, sharedPreferences, cacheListRestorer).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        verifyAll();
    }

    @Test
    public void createNull() throws Exception {
        expect(sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(ActivityType.NONE.name());
        cacheListRestorer.restore();

        replayAll();
        new ActivityRestorer(parent, null, sharedPreferences, cacheListRestorer).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.CACHE_LIST);
        verifyAll();
    }

    @Test
    public void notNewTask() throws Exception {
        replayAll();
        new ActivityRestorer(null, null, null, cacheListRestorer).restore(0, ActivityType.NONE);
        verifyAll();
    }

    @Test
    public void createSearchOnlineIntent() throws Exception {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        Activity parent = createMock(Activity.class);

        String searchOnline = ActivityType.SEARCH_ONLINE.name();
        expect(sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(searchOnline);
        cacheListRestorer.restore();

        replayAll();
        new ActivityRestorer(parent, null, sharedPreferences, cacheListRestorer).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        verifyAll();
    }

    @Test
    public void activityTypeNone() throws Exception {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        Activity parent = createMock(Activity.class);

        String none = ActivityType.NONE.name();
        expect(
                sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY,
                        ActivityType.NONE.toString())).andReturn(none);

        replayAll();
        new ActivityRestorer(parent, null, sharedPreferences, cacheListRestorer).restore(
                Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        verifyAll();
    }

    @Test
    public void createViewCache() throws Exception {
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        Activity parent = createMock(Activity.class);
        GeocacheFromPreferencesFactory geocacheFromPreferencesFactory = createMock(GeocacheFromPreferencesFactory.class);
        Geocache geocache = createMock(Geocache.class);
        Intent intent = createMock(Intent.class);

        String viewCache = ActivityType.VIEW_CACHE.name();
        expect(sharedPreferences.getString(ActivitySaver.LAST_ACTIVITY, ActivityType.NONE.name()))
                .andReturn(viewCache);
        expect(geocacheFromPreferencesFactory.create(sharedPreferences)).andReturn(geocache);
        expectNew(Intent.class, parent, CompassActivity.class).andReturn(intent);
        expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        parent.startActivity(intent);

        replayAll();
        new ActivityRestorer(parent, geocacheFromPreferencesFactory, sharedPreferences,
                cacheListRestorer).restore(Intent.FLAG_ACTIVITY_NEW_TASK, ActivityType.NONE);
        verifyAll();
    }
}
