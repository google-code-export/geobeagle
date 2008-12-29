
package com.google.code.geobeagle.intents;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.Destination;
import com.google.code.geobeagle.ui.LocationSetter;

import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class IntentStarterRadarTest extends TestCase {

    public void testStartIntent() {
        Intent intent = createMock(Intent.class);
        Context context = createMock(Context.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        Destination destination = createMock(Destination.class);

        expect(locationSetter.getDestination()).andReturn(destination);
        expect(intentFactory.createIntent("com.google.android.radar.SHOW_RADAR")).andReturn(intent);
        expect(destination.getLatitude()).andReturn(37.175d);
        expect(intent.putExtra("latitude", 37.175f)).andReturn(intent);
        expect(destination.getLongitude()).andReturn(122.8375d);
        expect(intent.putExtra("longitude", 122.8375f)).andReturn(intent);
        context.startActivity(intent);

        replay(locationSetter);
        replay(destination);
        replay(intentFactory);
        replay(intent);
        replay(context);
        new IntentStarterRadar(context, intentFactory, locationSetter).startIntent();
        verify(locationSetter);
        verify(destination);
        verify(intentFactory);
        verify(intent);
        verify(context);
    }
}
