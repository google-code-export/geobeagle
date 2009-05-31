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

package com.google.code.geobeagle.mainactivity.ui;

import static org.easymock.EasyMock.expect;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.ResourceProvider;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    TextView.class
})
public class OnContentProviderSelectedListenerTest {
    @Test
    public void testOnContentProviderSelectedListener() {
        TextView contentProviderCaption = PowerMock.createMock(TextView.class);
        ResourceProvider resourceProvider = PowerMock.createMock(ResourceProvider.class);
        expect(resourceProvider.getString(R.string.search_for)).andReturn("Search for");
        EasyMock.expectLastCall().times(2);

        expect(resourceProvider.getStringArray(R.array.object_names)).andReturn(new String[] {
                "letterbox", "geocache"
        });
        EasyMock.expectLastCall().times(2);
        contentProviderCaption.setText("Search for letterbox:");
        contentProviderCaption.setText("Search for geocache:");

        PowerMock.replayAll();
        OnContentProviderSelectedListener ocpsl = new OnContentProviderSelectedListener(
                resourceProvider, contentProviderCaption);
        ocpsl.onItemSelected(null, null, 0, 0);
        ocpsl.onItemSelected(null, null, 1, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnNothingSelected() {
        OnContentProviderSelectedListener ocpsl = new OnContentProviderSelectedListener(null, null);
        ocpsl.onNothingSelected(null);
    }
}
