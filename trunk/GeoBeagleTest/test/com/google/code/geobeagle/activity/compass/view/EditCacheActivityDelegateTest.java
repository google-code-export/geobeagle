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

package com.google.code.geobeagle.activity.compass.view;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.CacheType;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GeocacheFactory.Source;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.GeocacheListController;
import com.google.code.geobeagle.activity.compass.view.CancelButtonOnClickListener;
import com.google.code.geobeagle.activity.compass.view.EditCache;
import com.google.code.geobeagle.activity.compass.view.EditCacheActivityDelegate;
import com.google.code.geobeagle.activity.compass.view.SetButtonOnClickListener;
import com.google.code.geobeagle.database.LocationSaver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Activity.class, EditText.class, Intent.class, SetButtonOnClickListener.class,
        EditCacheActivityDelegate.class
})
public class EditCacheActivityDelegateTest {

    @Test
    public void testCancelButtonOnClickListener() {
        Activity activity = PowerMock.createMock(Activity.class);

        activity.setResult(Activity.RESULT_CANCELED, null);
        activity.finish();

        replay(activity);
        CancelButtonOnClickListener cancelButtonOnClickListener = new CancelButtonOnClickListener(
                activity);
        cancelButtonOnClickListener.onClick(null);
        verifyAll();
    }

    @Test
    public void testEditCacheActivityDelegateOnCreate() {
        Activity activity = createMock(Activity.class);

        activity.setContentView(R.layout.cache_edit);

        PowerMock.replayAll();
        new EditCacheActivityDelegate(activity, null, null, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void testEditCacheActivityOnResume() throws Exception {
        Activity activity = createMock(Activity.class);
        Intent intent = createMock(Intent.class);
        GeocacheFactory geocacheFactory = createMock(GeocacheFactory.class);
        Geocache geocache = createMock(Geocache.class);
        EditText id = createMock(EditText.class);
        EditText name = createMock(EditText.class);
        EditText latitude = createMock(EditText.class);
        EditText longitude = createMock(EditText.class);
        Button set = createMock(Button.class);
        SetButtonOnClickListener setButtonOnClickListener = PowerMock
                .createMock(SetButtonOnClickListener.class);
        EditCache editCache = createMock(EditCache.class);
        CancelButtonOnClickListener cancelButtonOnClickListener = PowerMock
                .createMock(CancelButtonOnClickListener.class);
        Button cancel = createMock(Button.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        expect(activity.getIntent()).andReturn(intent);
        expect(intent.<Geocache> getParcelableExtra("geocache")).andReturn(geocache);
        expect(activity.findViewById(R.id.edit_id)).andReturn(id);
        expect(activity.findViewById(R.id.edit_name)).andReturn(name);
        expect(activity.findViewById(R.id.edit_latitude)).andReturn(latitude);
        expect(activity.findViewById(R.id.edit_longitude)).andReturn(longitude);
        expectNew(EditCache.class, geocacheFactory, id, name, latitude, longitude)
                .andReturn(editCache);
        editCache.set(geocache);

        expectNew(SetButtonOnClickListener.class, activity, editCache, locationSaver)
                .andReturn(setButtonOnClickListener);
        expect(activity.findViewById(R.id.edit_set)).andReturn(set);
        set.setOnClickListener(setButtonOnClickListener);

        expect(activity.findViewById(R.id.edit_cancel)).andReturn(cancel);
        cancel.setOnClickListener(cancelButtonOnClickListener);

        replayAll();
        new EditCacheActivityDelegate(activity, cancelButtonOnClickListener, geocacheFactory,
                locationSaver).onResume();
        PowerMock.verifyAll();
        verifyAll();
    }

    @Test
    public void testGeocacheViewGetAndSet() {
        EditText id = createMock(EditText.class);
        EditText name = createMock(EditText.class);
        EditText latitude = createMock(EditText.class);
        EditText longitude = createMock(EditText.class);
        GeocacheFactory geocacheFactory = createMock(GeocacheFactory.class);
        Geocache geocache = createMock(Geocache.class);

        expect(geocache.getId()).andReturn("GC123");
        id.setText("GC123");
        expect(geocache.getName()).andReturn("a cache");
        name.setText("a cache");
        expect(geocache.getLatitude()).andReturn(37.5);
        latitude.setText("37 30.000");
        expect(geocache.getLongitude()).andReturn(-122.4);
        longitude.setText("-122 24.000");

        expect(latitude.requestFocus()).andReturn(true);
        StubEditable editableId = new StubEditable("GC123");
        expect(id.getText()).andReturn(editableId);
        StubEditable editableName = new StubEditable("a cache");
        expect(name.getText()).andReturn(editableName);
        StubEditable editableLatitude = new StubEditable("37 0.00");
        expect(latitude.getText()).andReturn(editableLatitude);
        StubEditable editableLongitude = new StubEditable("-122 30.000");
        expect(longitude.getText()).andReturn(editableLongitude);
        expect(geocache.getSourceType()).andReturn(Source.GPX);
        expect(geocache.getSourceName()).andReturn("source");
        expect(geocache.getCacheType()).andReturn(CacheType.TRADITIONAL);
        expect(geocache.getDifficulty()).andReturn(3);
        expect(geocache.getTerrain()).andReturn(2);
        expect(geocache.getContainer()).andReturn(4);
        expect(geocache.getAvailable()).andReturn(true);
        expect(geocache.getArchived()).andReturn(false);

        expect(
                geocacheFactory.create(editableId, editableName, 37, -122.5, Source.GPX, "source",
                        CacheType.TRADITIONAL, 3, 2, 4, true, false)).andReturn(geocache);

        replayAll();
        EditCache editCache = new EditCache(geocacheFactory, id, name, latitude, longitude);
        editCache.set(geocache);
        assertEquals(geocache, editCache.get());
        verifyAll();
    }

    @Test
    public void testSetButtonOnClickListener() throws Exception {
        Activity activity = createMock(Activity.class);
        EditCache editCache = createMock(EditCache.class);
        Intent intent = createMock(Intent.class);
        Geocache geocache = createMock(Geocache.class);
        LocationSaver locationSaver = createMock(LocationSaver.class);

        locationSaver.saveLocation(geocache);
        expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        expectNew(Intent.class).andReturn(intent);
        expect(editCache.get()).andReturn(geocache);
        expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();

        replayAll();
        SetButtonOnClickListener setButtonOnClickListener = new SetButtonOnClickListener(activity,
                editCache, locationSaver);
        setButtonOnClickListener.onClick(null);
        verifyAll();
    }
}
