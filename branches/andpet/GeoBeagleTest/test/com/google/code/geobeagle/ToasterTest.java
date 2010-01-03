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

package com.google.code.geobeagle;

import com.google.code.geobeagle.Toaster;
import com.google.code.geobeagle.Toaster.OneTimeToaster;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.widget.Toast;

@PrepareForTest(Toast.class)
@RunWith(PowerMockRunner.class)
public class ToasterTest {
    @Test
    public void testNoRetoast() {
        Toaster toaster = PowerMock.createMock(Toaster.class);
        toaster.showToast();

        PowerMock.replayAll();
        final OneTimeToaster oneTimeToaster = new OneTimeToaster(toaster);
        oneTimeToaster.showToast(true);
        oneTimeToaster.showToast(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testOneTimeToaster() {
        Toaster toaster = PowerMock.createMock(Toaster.class);
        toaster.showToast();

        PowerMock.replayAll();
        final OneTimeToaster oneTimeToaster = new OneTimeToaster(toaster);
        oneTimeToaster.showToast(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testToaster() {
        PowerMock.mockStatic(Toast.class);
        Toast toast = PowerMock.createMock(Toast.class);

        EasyMock.expect(Toast.makeText(null, 0, 0)).andReturn(toast);
        toast.show();

        PowerMock.replayAll();
        Toaster toaster = new Toaster(null, 0, 0);
        toaster.showToast();
        PowerMock.verifyAll();
    }
}
