
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

public class IntentStarterViewUriTest extends TestCase {

    public void testStartIntent() {
        Context context = createMock(Context.class);
        IntentFactory intentFactory = createMock(IntentFactory.class);
        LocationSetter locationSetter = createMock(LocationSetter.class);
        DestinationToUri destinationToUri = createMock(DestinationToUri.class);
        Intent intent = createMock(Intent.class);

        Destination destination = createMock(Destination.class);
        expect(locationSetter.getDestination()).andReturn(destination);
        expect(destinationToUri.convert(destination)).andReturn("destination uri");
        expect(intentFactory.createIntent(Intent.ACTION_VIEW, "destination uri"))
                .andReturn(intent);
        context.startActivity(intent);

        replay(locationSetter);
        replay(destinationToUri);
        replay(intentFactory);
        replay(context);
        new IntentStarterViewUri(context, intentFactory, locationSetter, destinationToUri)
                .startIntent();
        verify(locationSetter);
        verify(destinationToUri);
        verify(intentFactory);
        verify(context);
    }

}
