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

package com.google.code.geobeagle.location;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.LocationAndDirection;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.view.MyLocationProvider;

import org.junit.Test;

import android.location.Location;

public class MyLocationProviderTest {
    @Test
    public void test() {
        LocationAndDirection locationAndDirection = createMock(LocationAndDirection.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Location location = createMock(Location.class);

        expect(locationAndDirection.getLocation()).andReturn(location);

        replay(locationAndDirection);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationAndDirection,
                errorDisplayer);
        assertEquals(location, myLocationProvider.getLocation());
        verify(locationAndDirection);
    }

    @Test
    public void testNullLocation() {
        LocationAndDirection locationAndDirection = createMock(LocationAndDirection.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        expect(locationAndDirection.getLocation()).andReturn(null);

        errorDisplayer.displayError(R.string.error_cant_get_location);

        replay(locationAndDirection);
        replay(errorDisplayer);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationAndDirection,
                errorDisplayer);
        assertEquals(null, myLocationProvider.getLocation());
        verify(locationAndDirection);
        verify(errorDisplayer);
    }
}
