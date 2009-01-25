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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.MyLocationProvider;

import android.location.Location;

import junit.framework.TestCase;

public class MyLocationProviderTest extends TestCase {
    public void testNullLocation() {
        LocationControl locationControl = createMock(LocationControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        expect(locationControl.getLocation()).andReturn(null);

        errorDisplayer.displayError(R.string.error_cant_get_location);

        replay(locationControl);
        replay(errorDisplayer);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControl,
                errorDisplayer);
        assertEquals(null, myLocationProvider.getLocation());
        verify(locationControl);
        verify(errorDisplayer);
    }

    public void test() {
        LocationControl locationControl = createMock(LocationControl.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Location location = createMock(Location.class);

        expect(locationControl.getLocation()).andReturn(location);

        replay(locationControl);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControl,
                errorDisplayer);
        assertEquals(location, myLocationProvider.getLocation());
        verify(locationControl);
    }
}
