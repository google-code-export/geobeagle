
package com.google.code.geobeagle;

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
import java.util.List;

public class LocationSetterImpl implements LocationSetter {
    private static final String FNAME_RECENT_LOCATIONS = "RECENT_LOCATIONS";
    private final DescriptionsAndLocations mDescriptionsAndLocations;
    private final MockableEditText mTxtLocation;
    private final GpsControl mGpsControl;

    public LocationSetterImpl(Context context, MockableEditText editText, GpsControl gpsControl) {
        mTxtLocation = editText;
        this.mGpsControl = gpsControl;
        mDescriptionsAndLocations = new DescriptionsAndLocations();
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveLocation();
                }
            }
        });
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

    public void load(Context c) {
        try {
            mDescriptionsAndLocations.clear();
            final FileInputStream f = c.openFileInput(FNAME_RECENT_LOCATIONS);
            final InputStreamReader isr = new InputStreamReader(f);
            final BufferedReader br = new BufferedReader(isr);
            CharSequence dataLine = null;
            while ((dataLine = br.readLine()) != null) {
                saveLocation(dataLine);
            }
            f.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void save(Context c) {
        try {
            final FileOutputStream openFileOutput = c.openFileOutput(FNAME_RECENT_LOCATIONS,
                    Context.MODE_PRIVATE);
            final BufferedOutputStream bos = new BufferedOutputStream(openFileOutput);
            for (final CharSequence location : mDescriptionsAndLocations.getPreviousLocations()) {
                bos.write((location.toString() + "\n").getBytes());
            }
            bos.close();
            openFileOutput.close();
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
        final Destination d = new Destination(location);
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
        final CharSequence latLonText = Util.degreesToMinutes(lat) + "  "
                + Util.degreesToMinutes(lon) + " # " + description;
        mTxtLocation.setText(latLonText);
        saveLocation(latLonText);
        return latLonText;
    }

    public DescriptionsAndLocations getDescriptionsAndLocations() {
        return mDescriptionsAndLocations;
    }
}
