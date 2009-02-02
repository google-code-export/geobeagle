
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.DescriptionsAndLocations;
import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.LifecycleManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class LocationBookmarks implements LifecycleManager {
    private final Context mContext;
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final Pattern[] mDestinationPatterns;

    public LocationBookmarks(Context context, DescriptionsAndLocations descriptionsAndLocations,
            Pattern destinationPatterns[]) {
        mContext = context;
        mDescriptionsAndLocations = descriptionsAndLocations;
        mDestinationPatterns = destinationPatterns;
    }

    protected BufferedOutputStream createBufferedOutputStream(OutputStream outputStream) {
        return new BufferedOutputStream(outputStream);
    }

    protected BufferedReader createBufferedReader(InputStreamReader inputStreamReader) {
        return new BufferedReader(inputStreamReader);
    }

    protected InputStreamReader createInputStreamReader(FileInputStream fileInputStream) {
        return new InputStreamReader(fileInputStream);
    }

    private void readBookmarks() {
        try {
            mDescriptionsAndLocations.clear();
            final FileInputStream fileInputStream = mContext
                    .openFileInput(LocationSetter.FNAME_RECENT_LOCATIONS);
            final InputStreamReader inputStreamReader = createInputStreamReader(fileInputStream);
            final BufferedReader bufferedReader = createBufferedReader(inputStreamReader);
            CharSequence dataLine = null;
            while ((dataLine = bufferedReader.readLine()) != null) {
                saveLocation(dataLine);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveBookmarks() {
        try {
            final FileOutputStream fileOutputStream = mContext.openFileOutput(
                    LocationSetter.FNAME_RECENT_LOCATIONS, Context.MODE_PRIVATE);
            final BufferedOutputStream bufferedOutputStream = createBufferedOutputStream(fileOutputStream);

            for (final CharSequence location : mDescriptionsAndLocations.getPreviousLocations()) {
                bufferedOutputStream.write((location.toString() + "\n").getBytes());
            }
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onPause(Editor editor) {
        saveBookmarks();
    }

    public void onResume(SharedPreferences preferences, ErrorDisplayer errorDisplayer) {
        readBookmarks();
    }

    void saveLocation(final CharSequence location) {
        final Destination d = new Destination(location, mDestinationPatterns);
        final CharSequence description = d.getDescription();
        mDescriptionsAndLocations.add(description, location);
    }
}
