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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.widget.Spinner;

@RunWith(PowerMockRunner.class)
public class ContentSelectorTest {

    @Test
    public void testGetIndex() {
        Spinner spinner = PowerMock.createMock(Spinner.class);
        expect(spinner.getSelectedItemPosition()).andReturn(17);

        PowerMock.replayAll();
        ContentSelector contentSelector = new ContentSelector(spinner, null);
        assertEquals(17, contentSelector.getIndex());
        PowerMock.verifyAll();
    }

}
