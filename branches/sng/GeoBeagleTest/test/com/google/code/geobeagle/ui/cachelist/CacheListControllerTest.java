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

package com.google.code.geobeagle.ui.cachelist;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.cachelist.GeocacheListController.CacheListOnCreateContextMenuListener;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.ListActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        GeocacheListController.class, ListActivity.class, GeocacheListPresenter.class
})
public class CacheListControllerTest {

    @Test
    public void testCacheListOnCreateContextMenuListener() {
        ContextMenu menu = PowerMock.createMock(ContextMenu.class);
        AdapterContextMenuInfo menuInfo = PowerMock.createMock(AdapterContextMenuInfo.class);
        GeocacheVectors geocacheVectors = PowerMock.createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = PowerMock.createMock(GeocacheVector.class);

        expect(geocacheVectors.get(12)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(menu.setHeaderTitle("GC123")).andReturn(menu);
        expect(menu.add(0, GeocacheListController.MENU_VIEW, 0, "View")).andReturn(null);
        expect(menu.add(0, GeocacheListController.MENU_DELETE, 1, "Delete")).andReturn(null);

        PowerMock.replayAll();
        menuInfo.position = 12;
        new CacheListOnCreateContextMenuListener(geocacheVectors).onCreateContextMenu(menu, null,
                menuInfo);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelected() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        AdapterContextMenuInfo adapterContextMenuInfo = PowerMock
                .createMock(AdapterContextMenuInfo.class);
        ContextAction contextAction = PowerMock.createMock(ContextAction.class);

        ContextAction contextActions[] = {
                null, null, contextAction
        };
        expect(menuItem.getMenuInfo()).andReturn(adapterContextMenuInfo);
        expect(menuItem.getItemId()).andReturn(2);
        contextAction.act(76);

        PowerMock.replayAll();
        adapterContextMenuInfo.position = 76;
        GeocacheListController geocacheListController = new GeocacheListController(null, null,
                contextActions);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnContextItemSelectedError() {
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);

        RuntimeException runtimeException = new RuntimeException();
        expect(menuItem.getMenuInfo()).andThrow(runtimeException);
        errorDisplayer.displayErrorAndStack(runtimeException);

        PowerMock.replayAll();
        GeocacheListController geocacheListController = new GeocacheListController(errorDisplayer,
                null, null);
        assertTrue(geocacheListController.onContextItemSelected(menuItem));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClick() {
        final ContextAction contextAction = PowerMock.createMock(ContextAction.class);
        ContextAction contextActions[] = {
                null, contextAction
        };

        contextAction.act(46);

        PowerMock.replayAll();
        new GeocacheListController(null, null, contextActions).onListItemClick(null, null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnListItemClickError() {
        final ContextAction contextAction = PowerMock.createMock(ContextAction.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        ContextAction contextActions[] = {
                null, contextAction
        };
        RuntimeException runtimeException = new RuntimeException();
        contextAction.act(46);
        EasyMock.expectLastCall().andThrow(runtimeException);
        errorDisplayer.displayErrorAndStack(runtimeException);

        PowerMock.replayAll();
        new GeocacheListController(errorDisplayer, null, contextActions).onListItemClick(null,
                null, 46, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnOptionsItemSelected() {
        MenuActions menuActions = PowerMock.createMock(MenuActions.class);
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);

        expect(menuItem.getItemId()).andReturn(27);
        menuActions.act(27);

        PowerMock.replayAll();
        new GeocacheListController(null, menuActions, null).onOptionsItemSelected(menuItem);
        PowerMock.verifyAll();
    }
}
