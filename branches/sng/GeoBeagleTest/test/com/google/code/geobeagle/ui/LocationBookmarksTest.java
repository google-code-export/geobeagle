
package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.DescriptionsAndLocations;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import junit.framework.TestCase;

public class LocationBookmarksTest extends TestCase {

    public void testReadBookmarks() throws IOException {
        Context context = createMock(Context.class);
        FileInputStream fileInputStream = createMock(FileInputStream.class);
        final InputStreamReader inputStreamReader = createMock(InputStreamReader.class);
        final BufferedReader bufferedReader = createMock(BufferedReader.class);

        expect(context.openFileInput(LocationSetter.FNAME_RECENT_LOCATIONS)).andReturn(
                fileInputStream);
        expect(bufferedReader.readLine()).andReturn(null);
        fileInputStream.close();
        bufferedReader.close();
        inputStreamReader.close();

        replay(context);
        replay(fileInputStream);
        replay(inputStreamReader);
        replay(bufferedReader);
        LocationBookmarks locationBookmarks = new LocationBookmarks(context,
                new DescriptionsAndLocations(), null) {
            protected InputStreamReader createInputStreamReader(FileInputStream fileInputStream) {
                return inputStreamReader;
            }

            protected BufferedReader createBufferedReader(InputStreamReader inputStreamReader) {
                return bufferedReader;
            }
        };
        locationBookmarks.onResume(null, null);
        verify(context);
        verify(fileInputStream);
        verify(inputStreamReader);
        verify(bufferedReader);
    }

    public void testSaveBookmarks() throws IOException {
        final String LOCATION1 = "37 11.1 122 22.2";
        final String LOCATION2 = "38 33.3 122 44.4";

        MockableEditText editText = createMock(MockableEditText.class);
        Context context = createMock(Context.class);
        FileOutputStream fileOutputStream = createMock(FileOutputStream.class);
        final BufferedOutputStream bufferedOutputStream = createStrictMock(BufferedOutputStream.class);

        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("foobar", "37 11.1 122 22.2");
        descriptionsAndLocations.add("baz", "38 33.3 122 44.4");

        expect(context.openFileOutput(LocationSetter.FNAME_RECENT_LOCATIONS, Context.MODE_PRIVATE))
                .andReturn(fileOutputStream);

        bufferedOutputStream.write(aryEq((LOCATION1 + "\n").getBytes()));
        bufferedOutputStream.write(aryEq((LOCATION2 + "\n").getBytes()));
        bufferedOutputStream.close();
        fileOutputStream.close();

        replay(editText);
        replay(context);
        replay(fileOutputStream);
        replay(bufferedOutputStream);
        LocationBookmarks locationBookmarks = new LocationBookmarks(context,
                descriptionsAndLocations, null) {
            protected BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
                return bufferedOutputStream;
            }
        };
        locationBookmarks.onPause(null);
        assertEquals(LOCATION1, descriptionsAndLocations.getPreviousLocations().get(0));
        assertEquals(LOCATION2, descriptionsAndLocations.getPreviousLocations().get(1));
        verify(editText);
        verify(context);
        verify(fileOutputStream);
        verify(bufferedOutputStream);
    }

}
