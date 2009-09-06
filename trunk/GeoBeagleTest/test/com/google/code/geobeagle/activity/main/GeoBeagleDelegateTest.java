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

package com.google.code.geobeagle.activity.main;

import static org.junit.Assert.*;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.ActivityType;
import com.google.code.geobeagle.activity.MenuAction;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.FieldNoteResources;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import java.util.HashMap;

@PrepareForTest( {
        Bundle.class, FieldNoteResources.class, FieldNoteSender.class, GeoBeagleDelegate.class
})
@RunWith(PowerMockRunner.class)
public class GeoBeagleDelegateTest {
    @Test
    public void onCreate() {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        Button button = PowerMock.createMock(Button.class);

        EasyMock.expect(geoBeagle.findViewById(R.id.cache_details)).andReturn(button);
        CacheDetailsOnClickListener onClickListener = PowerMock
                .createMock(CacheDetailsOnClickListener.class);
        button.setOnClickListener(onClickListener);

        PowerMock.replayAll();
        new GeoBeagleDelegate(geoBeagle, null, null, onClickListener, null, null, null, null, null)
                .onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void onCreateDialogFind() throws Exception {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        FieldNoteSender fieldNoteSender = PowerMock.createMock(FieldNoteSender.class);
        Dialog dialog = PowerMock.createMock(Dialog.class);
        FieldNoteResources fieldNoteResources = PowerMock.createMock(FieldNoteResources.class);
        Resources resources = PowerMock.createMock(Resources.class);

        EasyMock.expect(geocache.getId()).andReturn("GC123");
        PowerMock.expectNew(FieldNoteResources.class, resources, R.id.menu_log_dnf).andReturn(
                fieldNoteResources);
        EasyMock.expect(fieldNoteSender.createDialog("GC123", fieldNoteResources, geoBeagle))
                .andReturn(dialog);

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(geoBeagle, null, null,
                null, fieldNoteSender, null, resources, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onCreateDialog(R.id.menu_log_dnf);
        PowerMock.verifyAll();
    }

    @Test
    public void onOptionsItemSelected() {
        MenuAction menuAction = PowerMock.createMock(MenuAction.class);
        MenuItem item = PowerMock.createMock(MenuItem.class);

        EasyMock.expect(item.getItemId()).andReturn(12);
        menuAction.act();

        PowerMock.replayAll();
        HashMap<Integer, MenuAction> menuActions = new HashMap<Integer, MenuAction>(0);
        menuActions.put(12, menuAction);
        new GeoBeagleDelegate(null, null, null, null, null, menuActions, null, null, null)
                .onOptionsItemSelected(item);
        PowerMock.verifyAll();
    }

    @Test
    public void onPause() {
        AppLifecycleManager appLifecycleManager = PowerMock.createMock(AppLifecycleManager.class);
        ActivitySaver activitySaver = PowerMock.createMock(ActivitySaver.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        appLifecycleManager.onPause();
        activitySaver.save(ActivityType.VIEW_CACHE, geocache);

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, activitySaver,
                appLifecycleManager, null, null, null, null, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onPause();
        PowerMock.verifyAll();
    }

    @Test
    public void onResume() {
        RadarView radar = PowerMock.createMock(RadarView.class);
        SharedPreferences sharedPreferences = PowerMock.createMock(SharedPreferences.class);
        AppLifecycleManager appLifecycleManager = PowerMock.createMock(AppLifecycleManager.class);

        EasyMock.expect(sharedPreferences.getBoolean("imperial", false)).andReturn(true);
        radar.setUseMetric(false);
        appLifecycleManager.onResume();

        EasyMock.expect(sharedPreferences.getBoolean("imperial", false)).andReturn(false);
        radar.setUseMetric(true);
        appLifecycleManager.onResume();

        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null,
                appLifecycleManager, null, null, null, null, sharedPreferences, radar);
        geoBeagleDelegate.onResume();
        geoBeagleDelegate.onResume();
        PowerMock.verifyAll();
    }

    @Test
    public void setGet() {
        Geocache geocache = PowerMock.createMock(Geocache.class);

        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null, null, null,
                null, null, null, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        assertEquals(geocache, geoBeagleDelegate.getGeocache());
    }

    @Test
    public void testRestoreInstanceState() throws Exception {
        GeocacheFromParcelFactory geocacheFromParcelFactory = PowerMock
                .createMock(GeocacheFromParcelFactory.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        PowerMock.expectNew(GeocacheFactory.class).andReturn(geocacheFactory);
        PowerMock.expectNew(GeocacheFromParcelFactory.class, geocacheFactory).andReturn(
                geocacheFromParcelFactory);
        EasyMock.expect(geocacheFromParcelFactory.createFromBundle(bundle)).andReturn(geocache);

        PowerMock.replayAll();
        new GeoBeagleDelegate(null, null, null, null, null, null, null, null, null)
                .onRestoreInstanceState(bundle);
        PowerMock.verifyAll();
    }

    @Test
    public void saveInstanceState() {
        Bundle bundle = PowerMock.createMock(Bundle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        geocache.saveToBundle(bundle);
        PowerMock.replayAll();
        final GeoBeagleDelegate geoBeagleDelegate = new GeoBeagleDelegate(null, null, null, null, null, null, null, null, null);
        geoBeagleDelegate.setGeocache(geocache);
        geoBeagleDelegate.onSaveInstanceState(bundle);
        PowerMock.verifyAll();

    }
}
