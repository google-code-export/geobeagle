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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.data.Geocache;
import com.google.code.geobeagle.data.GeocacheVector;
import com.google.code.geobeagle.data.GeocacheVectors;

import org.junit.Test;

import android.content.Context;
import android.content.Intent;

public class CacheListActionsTest {

    @Test
    public void testActionView() {
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);
        GeocacheVectors geocacheVectors = createMock(GeocacheVectors.class);
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);
        Geocache geocache = createMock(Geocache.class);

        expect(geocacheVectors.get(34)).andReturn(geocacheVector);
        expect(geocacheVector.getGeocache()).andReturn(geocache);
        expect(intent.setAction(GeocacheListController.SELECT_CACHE)).andReturn(intent);
        expect(intent.putExtra("geocache", geocache)).andReturn(intent);
        context.startActivity(intent);

        replay(geocacheVectors);
        replay(geocacheVector);
        replay(context);
        replay(intent);
        new ContextActionView(geocacheVectors, context, intent).act(34);
        verify(context);
        verify(intent);
        verify(geocacheVectors);
        verify(geocacheVector);
    }

}
