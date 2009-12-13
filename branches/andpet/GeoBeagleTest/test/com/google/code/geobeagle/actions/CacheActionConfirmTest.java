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

package com.google.code.geobeagle.actions;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.Geocache;

import static org.easymock.EasyMock.anyObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;

@PrepareForTest({AlertDialog.class})
@RunWith(PowerMockRunner.class)
public class CacheActionConfirmTest {

    @Test
    public void testActionConfirm() {
        Geocache geocache = PowerMock.createMock(Geocache.class);
        Activity activity = PowerMock.createMock(Activity.class);
        AlertDialog.Builder builder = PowerMock.createMock(AlertDialog.Builder.class);
        CacheAction cacheAction = PowerMock.createMock(CacheAction.class);
        AlertDialog alertDialog = PowerMock.createMock(AlertDialog.class);

        expect(geocache.getId()).andReturn("GC123").anyTimes();
        expect(geocache.getName()).andReturn("my cache").anyTimes();
        expect(builder.setTitle("title GC123")).andReturn(builder);
        expect(builder.setMessage("body GC123 my cache")).andReturn(builder);
        expect(builder.setPositiveButton((String)anyObject(), (OnClickListener)anyObject())).andReturn(builder);
        expect(builder.setNegativeButton((String)anyObject(), (OnClickListener)anyObject())).andReturn(builder);
        expect(builder.create()).andReturn(alertDialog);
        alertDialog.setOwnerActivity(activity);
        alertDialog.show();
        
        PowerMock.replayAll();
        new CacheActionConfirm(activity, builder, cacheAction, 
                "title %1$s", "body %1$s %2$s").act(geocache);
        PowerMock.verifyAll();
    }

}
