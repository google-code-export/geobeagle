
package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ui.ContentSelector;
import com.google.code.geobeagle.ui.ErrorDisplayer;

import android.content.SharedPreferences;

import junit.framework.TestCase;

public class AppLifecycleManagerTest extends TestCase {

    public void testOnPause() {
        LifecycleManager lifecycleManager1 = createMock(LifecycleManager.class);
        LifecycleManager lifecycleManager2 = createMock(LifecycleManager.class);
        SharedPreferences.Editor editor = createMock(SharedPreferences.Editor.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        AppLifecycleManager appLifecycleManager = new AppLifecycleManager(sharedPreferences,
                new LifecycleManager[] {
                        lifecycleManager2, lifecycleManager1
                });
        lifecycleManager1.onPause(editor);
        lifecycleManager2.onPause(editor);
        expect(sharedPreferences.edit()).andReturn(editor);
        expect(editor.commit()).andReturn(true);

        replay(sharedPreferences);
        replay(editor);
        replay(lifecycleManager1);
        replay(lifecycleManager2);
        appLifecycleManager.onPause();
        verify(sharedPreferences);
        verify(editor);
        verify(lifecycleManager1);
        verify(lifecycleManager2);
    }

    public void testOnResume() {
        LifecycleManager gpsLifecycleManager = createMock(LocationLifecycleManager.class);
        SharedPreferences sharedPreferences = createMock(SharedPreferences.class);
        ErrorDisplayer errorDisplayer = createMock(ErrorDisplayer.class);
        ContentSelector contentSelector = createMock(ContentSelector.class);

        AppLifecycleManager appLifecycleManager = new AppLifecycleManager(sharedPreferences,
                new LifecycleManager[] {
                        contentSelector, gpsLifecycleManager,
                });
        gpsLifecycleManager.onResume(sharedPreferences, errorDisplayer);
        contentSelector.onResume(sharedPreferences, errorDisplayer);

        replay(sharedPreferences);
        replay(gpsLifecycleManager);
        replay(contentSelector);
        appLifecycleManager.onResume(errorDisplayer);
        verify(sharedPreferences);
        verify(gpsLifecycleManager);
        verify(contentSelector);
    }
}
