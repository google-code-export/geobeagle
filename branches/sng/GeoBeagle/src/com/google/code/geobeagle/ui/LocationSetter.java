
package com.google.code.geobeagle.ui;

import com.google.code.geobeagle.DescriptionsAndLocations;
import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.LocationControl;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Util;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

public class LocationSetter {
    public static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    private final Context mContext;
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final Pattern[] mDestinationPatterns;
    private final LocationControl mGpsControl;
    private final MockableEditText mTxtLocation;

    public LocationSetter(Context context, MockableEditText editText,
            LocationControl locationControl, Pattern destinationPatterns[]) {
        mTxtLocation = editText;
        mContext = context;
        mDestinationPatterns = destinationPatterns;
        mGpsControl = locationControl;
        mDescriptionsAndLocations = new DescriptionsAndLocations();
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveLocation();
                }
            }
        });
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

    public DescriptionsAndLocations getDescriptionsAndLocations() {
        return mDescriptionsAndLocations;
    }

    /*
     * (non-Javadoc)
     * @see com.google.code.geobeagle.ui.DestinationProvider#getDestination()
     */
    public Destination getDestination() {
        return new Destination(mTxtLocation.getText(), mDestinationPatterns);
    }

    public CharSequence getLocation() {
        return mTxtLocation.getText();
    }

    public List<CharSequence> getPreviousDescriptions() {
        return mDescriptionsAndLocations.getPreviousDescriptions();
    }

    public List<CharSequence> getPreviousLocations() {
        return mDescriptionsAndLocations.getPreviousLocations();
    }

    public void readBookmarks() {
        try {
            mDescriptionsAndLocations.clear();
            final FileInputStream fileInputStream = mContext.openFileInput(FNAME_RECENT_LOCATIONS);
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

    public void saveBookmarks() {
        try {
            final FileOutputStream fileOutputStream = mContext.openFileOutput(
                    FNAME_RECENT_LOCATIONS, Context.MODE_PRIVATE);
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

    private CharSequence saveLocation() {
        return saveLocation(getLocation());
    }

    private CharSequence saveLocation(final CharSequence location) {
        final Destination d = new Destination(location, mDestinationPatterns);
        final CharSequence description = d.getDescription();
        mDescriptionsAndLocations.add(description, location);
        return location;
    }

    public void setLocation(CharSequence c, ErrorDisplayer errorDisplayer) {
        if (c == null) {
            Location location = mGpsControl.getLocation();
            if (location == null) {
                errorDisplayer.displayError(R.string.current_location_null);
                return;
            }
            setLocation(location.getLatitude(), location.getLongitude(), String.format(
                    "[%1$tk:%1$tM] My Location", location.getTime()));
            return;
        }
        saveLocation(c);
        mTxtLocation.setText(c);
    }

    public CharSequence setLocation(double lat, double lon, CharSequence description) {
        final CharSequence latLonText = Util.formatDegreesAsDecimalDegreesString(lat) + ", "
                + Util.formatDegreesAsDecimalDegreesString(lon) + " (" + description + ")";
        mTxtLocation.setText(latLonText);
        saveLocation(latLonText);
        return latLonText;
    }

}
