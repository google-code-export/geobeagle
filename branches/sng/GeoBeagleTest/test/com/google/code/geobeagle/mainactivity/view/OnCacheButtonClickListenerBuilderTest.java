
package com.google.code.geobeagle.mainactivity.view;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.mainactivity.intents.IntentStarter;
import com.google.code.geobeagle.mainactivity.view.CacheButtonOnClickListener;
import com.google.code.geobeagle.mainactivity.view.OnCacheButtonClickListenerBuilder;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.widget.Button;

@PrepareForTest( {
    OnCacheButtonClickListenerBuilder.class
})
@RunWith(PowerMockRunner.class)
public class OnCacheButtonClickListenerBuilderTest {
    @Test
    public void testSet() throws Exception {
        CacheButtonOnClickListener cacheButtonOnClickListener = PowerMock
                .createMock(CacheButtonOnClickListener.class);
        IntentStarter intentStarter = PowerMock.createMock(IntentStarter.class);
        ErrorDisplayer errorDisplayer = PowerMock.createMock(ErrorDisplayer.class);
        Activity activity = PowerMock.createMock(Activity.class);
        Button button = PowerMock.createMock(Button.class);

        PowerMock.expectNew(CacheButtonOnClickListener.class, intentStarter, "error",
                errorDisplayer).andReturn(cacheButtonOnClickListener);
        EasyMock.expect(activity.findViewById(37)).andReturn(button);
        button.setOnClickListener(cacheButtonOnClickListener);

        PowerMock.replayAll();
        new OnCacheButtonClickListenerBuilder(activity, errorDisplayer).set(37, intentStarter,
                "error");
        PowerMock.verifyAll();
    }
}
