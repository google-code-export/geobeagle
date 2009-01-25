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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.ResourceProvider;
import com.google.code.geobeagle.ui.ErrorDialog;

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
