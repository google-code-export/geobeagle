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

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.CacheList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import junit.framework.TestCase;

public class GeocacheListOnClickListenerTest extends TestCase {
    public void testOnClickListener() {
        final Activity activity = createMock(Activity.class);
        final Intent intent = createMock(Intent.class);

        activity.startActivity(intent);
        replay(activity);
        GeocacheListOnClickListener geocacheListOnClickListener = new GeocacheListOnClickListener(
                activity) {
            @Override
            protected Intent createIntent(Context context, Class<?> cls) {
                assertEquals(CacheList.class, cls);
                assertEquals(activity, context);
                return intent;
            }
        };
        geocacheListOnClickListener.onClick(null);
        verify(activity);
    }
}
