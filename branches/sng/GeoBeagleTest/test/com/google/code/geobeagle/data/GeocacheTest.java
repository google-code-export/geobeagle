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

import com.google.code.geobeagle.data.Geocache.Provider;
import com.google.code.geobeagle.data.Geocache.Source;
import com.google.code.geobeagle.data.Geocache.Source.SourceFactory;
import com.google.code.geobeagle.data.di.GeocacheFactory;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcel;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Parcel.class, Bundle.class, Geocache.class
})
public class GeocacheTest {

    @Test
    public void testDescribeContents() {
        Geocache geocache = new Geocache(null, null, 0, 0, null, null);
        assertEquals(0, geocache.describeContents());
    }

    @Test
    public void testGetContentProvider() {
        Geocache geocache = new Geocache("GC123", null, 0, 0, null, null);
        assertEquals(Provider.GROUNDSPEAK, geocache.getContentProvider());

        geocache = new Geocache("LBabc", null, 0, 0, null, null);
        assertEquals(Provider.ATLAS_QUEST, geocache.getContentProvider());

        geocache = new Geocache("foo", null, 0, 0, null, null);
        assertEquals(Provider.MY_LOCATION, geocache.getContentProvider());
    }

    @Test
    public void testGetIdAndName() {
        Geocache geocache = new Geocache("", "hello", 0, 0, null, null);
        assertEquals("hello", geocache.getIdAndName());

        geocache = new Geocache("idonly", "", 0, 0, null, null);
        assertEquals("idonly", geocache.getIdAndName());

        geocache = new Geocache("id", "hello", 0, 0, null, null);
        assertEquals("id: hello", geocache.getIdAndName());

    }

    @Test
    public void testGetShortId() {
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.GPX, "alameda");
        assertEquals("123", geocache.getShortId());

        geocache = new Geocache("", "a cache", 37.5, -122.25, Source.GPX, "alameda");
        assertEquals("", geocache.getShortId());
    }
    
    @Test
    public void testGetters() {
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.GPX, "alameda");
        assertEquals("GC123", geocache.getId());
        assertEquals(37.5, geocache.getLatitude(), 0);
        assertEquals(-122.25, geocache.getLongitude(), 0);
        assertEquals("a cache", geocache.getName());
        assertEquals("alameda", geocache.getSourceName());
        assertEquals(Source.GPX, geocache.getSourceType());
    }

    @Test
    public void testParcelConstructor() {
        Parcel parcel = PowerMock.createMock(Parcel.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(parcel.readBundle()).andReturn(bundle);
        EasyMock.expect(bundle.getCharSequence("id")).andReturn("GC123");
        EasyMock.expect(bundle.getDouble("latitude")).andReturn(new Double(37));
        EasyMock.expect(bundle.getDouble("longitude")).andReturn(new Double(-122));
        EasyMock.expect(bundle.getCharSequence("name")).andReturn("a cache");
        EasyMock.expect(bundle.getInt("sourceType")).andReturn(1);
        EasyMock.expect(bundle.getString("sourceName")).andReturn("new york city");
        EasyMock.expect(geocacheFactory.sourceFromInt(1)).andReturn(Source.GPX);
        EasyMock.expect(
                geocacheFactory.create("GC123", "a cache", 37f, -122, Source.GPX, "new york city"))
                .andReturn(geocache);

        PowerMock.replayAll();
        GeocacheFromParcelFactory geocacheFromParcelFactory = new GeocacheFromParcelFactory(
                geocacheFactory);
        assertEquals(geocache, geocacheFromParcelFactory.create(parcel));
        PowerMock.verifyAll();
    }

    @Test
    public void testProviderToInt() {
        assertEquals(1, Provider.GROUNDSPEAK.toInt());
    }

    @Test
    public void testSourceFromInt() {
        SourceFactory sourceFactory = new SourceFactory();
        assertEquals(Source.MY_LOCATION, sourceFactory.fromInt(1));
    }

    @Test
    public void testWriteToParcel() throws Exception {
        Parcel parcel = PowerMock.createMock(Parcel.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);

        PowerMock.expectNew(Bundle.class).andReturn(bundle);
        bundle.putCharSequence("id", "GC123");
        bundle.putCharSequence("name", "a cache");
        bundle.putDouble("latitude", 37.5);
        bundle.putDouble("longitude", -122.25);
        bundle.putInt("sourceType", Source.GPX.toInt());
        bundle.putString("sourceName", "alameda");
        parcel.writeBundle(bundle);

        PowerMock.replayAll();
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.GPX, "alameda");
        geocache.writeToParcel(parcel, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testWriteToPrefs() throws Exception {
        Editor editor = PowerMock.createMock(Editor.class);

        EasyMock.expect(editor.putString("id", "GC123")).andReturn(editor);
        EasyMock.expect(editor.putString("name", "a cache")).andReturn(editor);
        EasyMock.expect(editor.putFloat("latitude", 37.5f)).andReturn(editor);
        EasyMock.expect(editor.putFloat("longitude", -122.25f)).andReturn(editor);
        EasyMock.expect(editor.putInt("sourceType", Source.MY_LOCATION.toInt())).andReturn(editor);
        EasyMock.expect(editor.putString("sourceName", null)).andReturn(editor);

        PowerMock.replayAll();
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.MY_LOCATION,
                null);
        geocache.writeToPrefs(editor);
        PowerMock.verifyAll();
    }
}
