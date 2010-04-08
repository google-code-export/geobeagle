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

package com.google.code.geobeagle;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bitmap.class, Canvas.class, GraphicsGenerator.class
})
public class GraphicsGeneratorTest {

    @Test
    public void testCreateRating() throws Exception {
        Drawable unselected = PowerMock.createMock(Drawable.class);
        Drawable halfSelected = PowerMock.createMock(Drawable.class);
        Drawable selected = PowerMock.createMock(Drawable.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);
        BitmapDrawable bitmapDrawable = PowerMock
                .createMock(BitmapDrawable.class);

        EasyMock.expect(unselected.getIntrinsicWidth()).andReturn(10);
        EasyMock.expect(unselected.getIntrinsicHeight()).andReturn(5);
        PowerMock.mockStatic(Bitmap.class);
        EasyMock.expect(Bitmap.createBitmap(50, 16, Bitmap.Config.ARGB_8888))
                .andReturn(bitmap);
        PowerMock.expectNew(Canvas.class, bitmap).andReturn(canvas);

        selected.setBounds(0, 0, 9, 4);
        selected.draw(canvas);
        halfSelected.setBounds(10, 0, 19, 4);
        halfSelected.draw(canvas);
        unselected.setBounds(20, 0, 29, 4);
        unselected.draw(canvas);
        unselected.setBounds(30, 0, 39, 4);
        unselected.draw(canvas);
        unselected.setBounds(40, 0, 49, 4);
        unselected.draw(canvas);

        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(
                bitmapDrawable);

        PowerMock.replayAll();
        new GraphicsGenerator.RatingsGenerator().createRating(unselected,
                halfSelected, selected, 3);
        PowerMock.verifyAll();
    }
}
