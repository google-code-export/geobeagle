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

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ui.ErrorDisplayer;
import com.google.code.geobeagle.ui.MyLocationProvider;

import org.junit.Test;

import android.location.Location;

public class MyLocationProviderTest {
    @Test
    public void test() {
        LocationControlBuffered locationControlBuffered = createMock(LocationControlBuffered.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        Location location = createMock(Location.class);

        expect(locationControlBuffered.getLocation()).andReturn(location);

        replay(locationControlBuffered);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControlBuffered,
                errorDisplayer);
        assertEquals(location, myLocationProvider.getLocation());
        verify(locationControlBuffered);
    }

    @Test
    public void testNullLocation() {
        LocationControlBuffered locationControlBuffered = createMock(LocationControlBuffered.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        expect(locationControlBuffered.getLocation()).andReturn(null);

        errorDisplayer.displayError(R.string.error_cant_get_location);

        replay(locationControlBuffered);
        replay(errorDisplayer);
        MyLocationProvider myLocationProvider = new MyLocationProvider(locationControlBuffered,
                errorDisplayer);
        assertEquals(null, myLocationProvider.getLocation());
        verify(locationControlBuffered);
        verify(errorDisplayer);
    }
}
