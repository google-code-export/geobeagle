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
        assertEquals("alameda", geocache.getSourceName());
        assertEquals(Source.GPX, geocache.getSourceType());
        
        assertEquals(geocache.isAvailable(), true);
        assertEquals(geocache.isArchived(), false);      
        assertEquals("a cache", geocache.getName());
        
        geocache.setStatus(false, false);
        assertEquals(geocache.isAvailable(), false);
        assertEquals(geocache.isArchived(), false);
        assertEquals("<strike>a cache</strike>", geocache.getName());
        
        geocache.setStatus(true, false);
        assertEquals(geocache.isAvailable(), true);
        assertEquals(geocache.isArchived(), false);
        assertEquals("a cache", geocache.getName());
        
        geocache.setStatus(true, true);
        assertEquals(geocache.isAvailable(), true);
        assertEquals(geocache.isArchived(), true);
        assertEquals("<font color='red'><strike>a cache</strike></font>", geocache.getName());
        assertEquals("a cache", geocache.getNamePlain());


    }

    @Test
    public void testParcelConstructor() {
        Parcel parcel = PowerMock.createMock(Parcel.class);
        Bundle bundle = PowerMock.createMock(Bundle.class);
        GeocacheFactory geocacheFactory = PowerMock.createMock(GeocacheFactory.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);

        EasyMock.expect(parcel.readBundle()).andReturn(bundle);
        EasyMock.expect(bundle.getCharSequence(Geocache.ID)).andReturn("GC123");
        EasyMock.expect(bundle.getDouble(Geocache.LATITUDE)).andReturn(new Double(37));
        EasyMock.expect(bundle.getDouble(Geocache.LONGITUDE)).andReturn(new Double(-122));
        EasyMock.expect(bundle.getCharSequence(Geocache.NAME)).andReturn("a cache");
        EasyMock.expect(bundle.getInt(Geocache.SOURCE_TYPE)).andReturn(1);
        EasyMock.expect(bundle.getString(Geocache.SOURCE_NAME)).andReturn("new york city");
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
        assertEquals(0, Provider.ATLAS_QUEST.toInt());
        assertEquals(1, Provider.GROUNDSPEAK.toInt());
        assertEquals(-1, Provider.MY_LOCATION.toInt());
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
        bundle.putCharSequence(Geocache.ID, "GC123");
        bundle.putCharSequence(Geocache.NAME, "a cache");
        bundle.putDouble(Geocache.LATITUDE, 37.5);
        bundle.putDouble(Geocache.LONGITUDE, -122.25);
        bundle.putInt(Geocache.SOURCE_TYPE, Source.GPX.toInt());
        bundle.putString(Geocache.SOURCE_NAME, "alameda");
        parcel.writeBundle(bundle);

        PowerMock.replayAll();
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.GPX, "alameda");
        geocache.writeToParcel(parcel, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testWriteToPrefs() throws Exception {
        Editor editor = PowerMock.createMock(Editor.class);

        EasyMock.expect(editor.putString(Geocache.ID, "GC123")).andReturn(editor);
        EasyMock.expect(editor.putString(Geocache.NAME, "a cache")).andReturn(editor);
        EasyMock.expect(editor.putFloat(Geocache.LATITUDE, 37.5f)).andReturn(editor);
        EasyMock.expect(editor.putFloat(Geocache.LONGITUDE, -122.25f)).andReturn(editor);
        EasyMock.expect(editor.putInt(Geocache.SOURCE_TYPE, Source.MY_LOCATION.toInt())).andReturn(
                editor);
        EasyMock.expect(editor.putString(Geocache.SOURCE_NAME, null)).andReturn(editor);

        PowerMock.replayAll();
        Geocache geocache = new Geocache("GC123", "a cache", 37.5, -122.25, Source.MY_LOCATION,
                null);
        geocache.writeToPrefs(editor);
        PowerMock.verifyAll();
    }
    
    @Test
    public void testDetailedWriters() throws Exception {
        Geocache geocache = new Geocache();
        final Double lat = 35.49;
        final Double lon = -86.47;
        final String diff = "1.5";
        final String terr = "3.5";
        final String gcid = "GC1234";
        final String name = "Oozy Rat";
        final String shortdescription = "My non-rambling description";
        final String description = "My Rambling Description";
        final String type = "Traditional";
        final String container = "Micro";
        final String placer = "Robert, the Wonderful";
        final String placed = "04/03/2009";
        final String hint = "Under the billion parallel sticks";
        final String logs = "Eventually, this will be 'real' logs";
  
    	String html = "<b>" + gcid + "</b> - " + name + "<br >";
		html += "Placed " + placed + " by " + placer + "<br >";
		html += diff + "/" + terr +  " " + type + "/" + container + "<p />";
		html += shortdescription + "<br />" + description + "<hr />" + hint + "hr />" + logs;
  	
        // Available/archived is exercised in testGetters().
        geocache.setStatus(true, false);
   
        geocache.setCoords(lat, lon);
        geocache.setDiff(diff);
        geocache.setTerr(terr);
        geocache.setId(gcid);
        geocache.setName(name);
        geocache.setShortDesc(shortdescription, false);
        geocache.setLongDesc(description, false);
        geocache.setCacheType(type);
        geocache.setContainer(container);
        geocache.setPlacer(placer);
        geocache.setPlacementDate(placed);
        geocache.setHint(hint);
        geocache.setLogBlob(logs);
        
        assertEquals(geocache.getLatitude(), lat, 0);
        assertEquals(geocache.getLongitude(), lon, 0);
        assertEquals(geocache.getDiff(), diff);
        assertEquals(geocache.getTerr(), terr);
        assertEquals(geocache.getId(), gcid);
        assertEquals(geocache.getName(), name);
        assertEquals(geocache.getShortDesc(), shortdescription);
        assertEquals(geocache.getLongDesc(), description);
        assertEquals(geocache.getCacheType(), type);
        assertEquals(geocache.getContainer(), container);
        assertEquals(geocache.getPlacer(), placer);
        assertEquals(geocache.getPlacementDay(), placed);
        assertEquals(geocache.getHint(), hint);
        assertEquals(geocache.getLogBlob(), logs);
        assertEquals(geocache.asHtml(), html);

    }
}
