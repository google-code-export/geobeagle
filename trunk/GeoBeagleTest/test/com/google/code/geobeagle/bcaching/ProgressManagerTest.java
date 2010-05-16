
package com.google.code.geobeagle.bcaching;

import com.google.code.geobeagle.bcaching.progress.ProgressManager;
import com.google.code.geobeagle.bcaching.progress.ProgressMessage;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Message;
import android.util.Log;

@PrepareForTest( {
        Message.class, Log.class, ImportBCachingWorker.class
})
@RunWith(PowerMockRunner.class)
public class ProgressManagerTest {

    @Test
    public void testUpdate() {
        PowerMock.mockStatic(Message.class);
        Message message = PowerMock.createMock(Message.class);
        EasyMock.expect(Message.obtain(null, ProgressMessage.DONE.ordinal(), 0, 0)).andReturn(
                message);
        message.sendToTarget();

        PowerMock.replayAll();
        new ProgressManager().update(null, ProgressMessage.DONE, 0);
        PowerMock.verifyAll();
    }

}
