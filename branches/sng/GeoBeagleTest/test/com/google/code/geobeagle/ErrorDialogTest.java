package com.google.code.geobeagle;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.app.AlertDialog;

import junit.framework.TestCase;

public class ErrorDialogTest extends TestCase {
    public void testResource() {
        ResourceProvider resourceProvider = createMock(ResourceProvider.class);
        AlertDialog alertDialog = createMock(AlertDialog.class);
        ErrorDialog errorDialog = new ErrorDialog(alertDialog, resourceProvider);
        expect(resourceProvider.getString(37)).andReturn("some error message");
        alertDialog.setMessage("some error message");
        alertDialog.show();
        
        replay(resourceProvider);
        replay(alertDialog);
        errorDialog.show(37);
        verify(resourceProvider);
        verify(alertDialog);
    }
    
    public void testString() {
        AlertDialog alertDialog = createMock(AlertDialog.class);
        ErrorDialog errorDialog = new ErrorDialog(alertDialog, null);
        alertDialog.setMessage("another error message");
        alertDialog.show();
        
        replay(alertDialog);
        errorDialog.show("another error message");
        verify(alertDialog);
    }
}
