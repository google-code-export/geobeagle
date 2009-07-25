
package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.menu.MenuAction;
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
import android.content.res.Resources;
import android.view.MenuItem;
import android.widget.Button;

import java.util.HashMap;

@PrepareForTest( {
        FieldNoteResources.class, FieldNoteSender.class, GeoBeagleDelegate.class
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
        new GeoBeagleDelegate(geoBeagle, null, null, onClickListener, null, null, null).onCreate();
        PowerMock.verifyAll();
    }

    @Test
    public void onCreateDialogFind() throws Exception {
        GeoBeagle geoBeagle = PowerMock.createMock(GeoBeagle.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        EasyMock.expect(geoBeagle.getGeocache()).andReturn(geocache);
        EasyMock.expect(geocache.getId()).andReturn("GC123");
        FieldNoteSender fieldNoteSender = PowerMock.createMock(FieldNoteSender.class);
        Dialog dialog = PowerMock.createMock(Dialog.class);
        FieldNoteResources fieldNoteResources = PowerMock.createMock(FieldNoteResources.class);
        Resources resources = PowerMock.createMock(Resources.class);
        PowerMock.expectNew(FieldNoteResources.class, resources, R.id.menu_log_dnf).andReturn(
                fieldNoteResources);
        EasyMock.expect(fieldNoteSender.createDialog("GC123", fieldNoteResources, geoBeagle))
                .andReturn(dialog);

        PowerMock.replayAll();
        new GeoBeagleDelegate(geoBeagle, null, null, null, fieldNoteSender, null, resources)
                .onCreateDialog(R.id.menu_log_dnf);
        PowerMock.verifyAll();
    }

    @Test
    public void onOptionsItemSelected() {
        HashMap<Integer, MenuAction> menuActions = new HashMap<Integer, MenuAction>(0);
        MenuAction menuAction = PowerMock.createMock(MenuAction.class);
        menuActions.put(12, menuAction);
        menuAction.act();

        MenuItem item = PowerMock.createMock(MenuItem.class);
        EasyMock.expect(item.getItemId()).andReturn(12);

        PowerMock.replayAll();
        new GeoBeagleDelegate(null, null, null, null, null, menuActions, null)
                .onOptionsItemSelected(item);
        PowerMock.verifyAll();
    }
}
