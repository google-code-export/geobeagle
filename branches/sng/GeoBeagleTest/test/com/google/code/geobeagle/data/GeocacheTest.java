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

package com.google.code.geobeagle.data;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.data.di.GeocacheFromTextFactory;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Bundle;
import android.os.Parcel;

import java.util.regex.Pattern;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Parcel.class, Bundle.class, Geocache.class
})
public class GeocacheTest {

    private static final Pattern mGeocachePatterns[] = {
            Pattern.compile("(?:GC)(\\w*)"), Pattern.compile("(?:LB)(\\w*)")
    };

    @Test
    public void testBadCoordinatesGoodDescription() {
        Geocache destinationImpl = GeocacheFromTextFactory.create("  FOO (Description)",
                mGeocachePatterns);
        assertEquals(0.0, destinationImpl.getLatitude(), 0);
        assertEquals(0.0, destinationImpl.getLongitude(), 0);
        assertEquals("Description", destinationImpl.getIdAndName());
    }

    @Test
    public void testDescriptionGetIdAndName() {
        Geocache destinationImpl = GeocacheFromTextFactory.create(
                " \t 37 03.0, 122 00.0 (Description)", mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude(), 0);
        assertEquals(122.0, destinationImpl.getLongitude(), 0);
        assertEquals("Description", destinationImpl.getIdAndName());
    }

    @Test
    public void testEmptyDestination() {
        Geocache geocache = GeocacheFromTextFactory.create("", mGeocachePatterns);
        assertEquals(0.0, geocache.getLatitude(), 0);
        assertEquals(0.0, geocache.getLongitude(), 0);
        assertEquals("", geocache.getIdAndName());
        assertEquals("", geocache.getShortId());
    }

    @Test
    public void testExtractDescription() {
        assertEquals("GC123", GeocacheFromTextFactory.extractDescription("123 (GC123)"));
    }

    @Test
    public void testGetCoordinatesIdAndName() {
        Geocache destinationImpl = GeocacheFromTextFactory.create(
                "s37 03.0, 122 00.0 (Description)", mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude(), 0);
        assertEquals(122.0, destinationImpl.getLongitude(), 0);
        assertEquals("37 03.000, 122 00.000 (Description)", destinationImpl
                .getCoordinatesIdAndName());
    }

    @Test
    public void testGetId() {
        Geocache geocache = GeocacheFromTextFactory.create("34.313,122.43 (LB89882: The Nut Case)",
                mGeocachePatterns);
        assertEquals("89882", geocache.getShortId());
        assertEquals("LB89882", geocache.getId());
        assertEquals(1, geocache.getContentIndex());
        assertEquals("The Nut Case", geocache.getName());

        geocache = GeocacheFromTextFactory.create("34.313,122.43 (GCFOOBAR: GS cache)",
                mGeocachePatterns);
        assertEquals("FOOBAR", geocache.getShortId());
        assertEquals(0, geocache.getContentIndex());
        assertEquals("GCFOOBAR: GS cache", geocache.getIdAndName());
    }

    @Test
    public void testLatLong() {
        Geocache geocache = GeocacheFromTextFactory.create("37 00.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.0, geocache.getLatitude(), 0);
        assertEquals(122.0, geocache.getLongitude(), 0);
        assertEquals("", geocache.getName());

        Geocache ll2 = GeocacheFromTextFactory.create("37 00.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.0, ll2.getLatitude(), 0);
        assertEquals(122.0, ll2.getLongitude(), 0);

        Geocache ll3 = GeocacheFromTextFactory.create("37 03.0, 122 00.0", mGeocachePatterns);
        assertEquals(37.05, ll3.getLatitude(), 0);
        assertEquals(122.0, ll3.getLongitude(), 0);

        Geocache ll4 = GeocacheFromTextFactory.create(" \t 37 03.0, 122 00.0  ", mGeocachePatterns);
        assertEquals(37.05, ll4.getLatitude(), 0);
        assertEquals(122.0, ll4.getLongitude(), 0);
        assertEquals("", ll4.getIdAndName());
    }

    @Test
    public void testNoName() {
        Geocache destinationImpl = GeocacheFromTextFactory.create(
                " \t 37 03.0, 122 00.0 (GC12345)", mGeocachePatterns);
        assertEquals(37.05, destinationImpl.getLatitude(), 0);
        assertEquals(122.0, destinationImpl.getLongitude(), 0);
        assertEquals("GC12345", destinationImpl.getId());
        assertEquals("", destinationImpl.getName());
        assertEquals("GC12345", destinationImpl.getIdAndName());
    }

    @Test
    public void testParcelConstructor() {
        Parcel parcel = PowerMock.createMock(Parcel.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);

        EasyMock.expect(parcel.readBundle()).andReturn(bundle);
        EasyMock.expect(bundle.getInt("contentSelectorIndex")).andReturn(1);
        EasyMock.expect(bundle.getCharSequence("id")).andReturn("GC123");
        EasyMock.expect(bundle.getDouble("latitude")).andReturn(new Double(37));
        EasyMock.expect(bundle.getDouble("longitude")).andReturn(new Double(-122));
        EasyMock.expect(bundle.getCharSequence("name")).andReturn("a cache");

        PowerMock.replayAll();
        Geocache geocache = new Geocache(parcel);
        assertEquals(1, geocache.getContentIndex());
        assertEquals("GC123", geocache.getId());
        assertEquals("a cache", geocache.getName());
        assertEquals(37, geocache.getLatitude(), 0);
        assertEquals(-122, geocache.getLongitude(), 0);
        assertEquals(0, geocache.describeContents());
        PowerMock.verifyAll();
    }

    @Test
    public void testWriteToParcel() throws Exception {
        Parcel parcel = PowerMock.createMock(Parcel.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);

        PowerMock.expectNew(Bundle.class).andReturn(bundle);
        bundle.putInt("contentSelectorIndex", 1);
        bundle.putCharSequence("id", "GC123");
        bundle.putCharSequence("name", "a cache");
        bundle.putDouble("latitude", 37.5);
        bundle.putDouble("longitude", -122.25);
        parcel.writeBundle(bundle);

        PowerMock.replayAll();
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25);
        geocache.writeToParcel(parcel, 0);
        PowerMock.verifyAll();
    }
}
