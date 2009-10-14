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

package com.google.code.geobeagle.actions.context;

import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.activity.EditCacheActivity;
import com.google.code.geobeagle.activity.cachelist.actions.context.ContextActionEdit;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.content.Intent;

@PrepareForTest( {
    ContextActionEdit.class
})
@RunWith(PowerMockRunner.class)
public class ContextActionEditTest {
    @Test
    public void testAct() throws Exception {
        Context context = PowerMock.createMock(Context.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        PowerMock.expectNew(Intent.class, context, EditCacheActivity.class).andReturn(intent);
        EasyMock.expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        context.startActivity(intent);

        PowerMock.replayAll();
        new ContextActionEdit(context).act(geocache);
        PowerMock.verifyAll();
    }
}
