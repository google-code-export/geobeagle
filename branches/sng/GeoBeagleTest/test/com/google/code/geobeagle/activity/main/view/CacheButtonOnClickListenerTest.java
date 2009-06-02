
package com.google.code.geobeagle.activity.main.view;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.activity.main.intents.IntentStarter;
import com.google.code.geobeagle.activity.main.view.CacheButtonOnClickListener;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ActivityNotFoundException;

@RunWith(PowerMockRunner.class)
public class CacheButtonOnClickListenerTest {

    @Test
    public void testOnClick() {
        IntentStarter intentStarter = PowerMock.createMock(IntentStarter.class);

        intentStarter.startIntent();

        PowerMock.replayAll();
        new CacheButtonOnClickListener(intentStarter, null, null).onClick(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClick_ActivityNotFound() {
        IntentStarter intentStarter = PowerMock.createMock(IntentStarter.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        ActivityNotFoundException activityNotFoundException = PowerMock
                .createMock(ActivityNotFoundException.class);

        intentStarter.startIntent();
        EasyMock.expectLastCall().andThrow(activityNotFoundException);
        EasyMock.expect(activityNotFoundException.fillInStackTrace()).andReturn(
                activityNotFoundException);
        EasyMock.expect(activityNotFoundException.getMessage()).andReturn("no radar");
        errorDisplayer.displayError("Error: no radar problem");

        PowerMock.replayAll();
        new CacheButtonOnClickListener(intentStarter, " problem", errorDisplayer).onClick(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClick_RandomError() {
        IntentStarter intentStarter = PowerMock.createMock(IntentStarter.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        NumberFormatException numberFormatException = PowerMock
                .createMock(NumberFormatException.class);

        intentStarter.startIntent();
        EasyMock.expectLastCall().andThrow(numberFormatException);
        EasyMock.expect(numberFormatException.fillInStackTrace()).andReturn(
                numberFormatException);
        EasyMock.expect(numberFormatException.getMessage()).andReturn("no radar");
        errorDisplayer.displayError("Error: no radar");

        PowerMock.replayAll();
        new CacheButtonOnClickListener(intentStarter, " problem", errorDisplayer).onClick(null);
        PowerMock.verifyAll();
    }
}
