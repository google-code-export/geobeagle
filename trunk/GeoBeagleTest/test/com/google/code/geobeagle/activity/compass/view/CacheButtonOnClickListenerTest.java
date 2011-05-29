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

package com.google.code.geobeagle.activity.compass.view;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.compass.intents.IntentStarter;
import com.google.code.geobeagle.activity.compass.view.OnClickListenerIntentStarter;

import org.easymock.EasyMock;
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
        new OnClickListenerIntentStarter(intentStarter, null).onClick(null);
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
        errorDisplayer.displayError(R.string.error1, "no radar");

        PowerMock.replayAll();
        new OnClickListenerIntentStarter(intentStarter, errorDisplayer).onClick(null);
        PowerMock.verifyAll();
    }
}
