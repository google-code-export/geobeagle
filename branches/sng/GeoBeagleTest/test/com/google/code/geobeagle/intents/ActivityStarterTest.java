package com.google.code.geobeagle.intents;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.intents.ActivityStarter;

import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class ActivityStarterTest extends TestCase {

    public void testStartActivity() {
        Context context = createMock(Context.class);
        Intent intent = createMock(Intent.class);
        
        context.startActivity(intent);
        
        replay(context);
        new ActivityStarter(context).startActivity(intent); 
        verify(context);
    }

}
