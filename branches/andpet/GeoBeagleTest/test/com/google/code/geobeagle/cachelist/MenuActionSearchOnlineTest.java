
package com.google.code.geobeagle.cachelist;

import com.google.code.geobeagle.actions.MenuActionSearchOnline;
import com.google.code.geobeagle.activity.searchonline.SearchOnlineActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Intent;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    MenuActionSearchOnline.class
})
public class MenuActionSearchOnlineTest {
    @Test
    public void testAct() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Intent intent = PowerMock.createMock(Intent.class);

        PowerMock.expectNew(Intent.class, activity, SearchOnlineActivity.class).andReturn(intent);
        activity.startActivity(intent);

        PowerMock.replayAll();
        new MenuActionSearchOnline(activity, null).act();
        PowerMock.verifyAll();
    }
}
