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

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.GraphicsGenerator.AttributePainter;
import com.google.code.geobeagle.GraphicsGenerator.ListViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.MapViewBitmapCopier;
import com.google.code.geobeagle.GraphicsGenerator.RatingsArray;
import com.google.code.geobeagle.GraphicsGenerator.RatingsGenerator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bitmap.class, Canvas.class, Color.class, GraphicsGenerator.class, Rect.class
})
public class GraphicsGeneratorTest {
    @Before
    public void setUp() {
        PowerMock.mockStatic(Bitmap.class);
        PowerMock.mockStatic(Color.class);
    }

    @Test
    public void testAttributePainter() {
        Paint tempPaint = PowerMock.createMock(Paint.class);
        Rect tempRect = PowerMock.createMock(Rect.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);

        tempPaint.setColor(27);
        tempRect.set(0, 88, 160, 91);
        canvas.drawRect(tempRect, tempPaint);
        
        tempPaint.setColor(99);
        tempRect.set(0, 92, 120, 95);
        canvas.drawRect(tempRect, tempPaint);
        
        PowerMock.replayAll();
        AttributePainter attributePainter = new AttributePainter(tempPaint, tempRect);
        attributePainter.paintAttribute(1, 4, 100, 200, canvas, 8, 27);
        attributePainter.paintAttribute(0, 4, 100, 200, canvas, 6, 99);
        PowerMock.verifyAll();
    }

    @Test
    public void testListViewBitmapCopier() {
        Bitmap resourceBitmap = PowerMock.createMock(Bitmap.class);
        Bitmap copy = PowerMock.createMock(Bitmap.class);

        EasyMock.expect(resourceBitmap.getHeight()).andReturn(100).anyTimes();
        EasyMock.expect(resourceBitmap.getWidth()).andReturn(200).anyTimes();
        EasyMock.expect(Bitmap.createBitmap(200, 105, Bitmap.Config.ARGB_8888))
                .andReturn(copy);
        resourceBitmap.getPixels((int[])EasyMock.anyObject(), EasyMock.eq(0),
                EasyMock.eq(200), EasyMock.eq(0), EasyMock.eq(0), EasyMock
                        .eq(200), EasyMock.eq(100));
        copy.setPixels((int[])EasyMock.anyObject(), EasyMock.eq(0), EasyMock
                .eq(200), EasyMock.eq(0), EasyMock.eq(0), EasyMock.eq(200),
                EasyMock.eq(100));

        PowerMock.replayAll();
        assertEquals(copy, new ListViewBitmapCopier().copy(resourceBitmap));
        PowerMock.verifyAll();
    }
    
    @Test
    public void testListViewBitmapCopierGetBottom() {
        assertEquals(0, new ListViewBitmapCopier().getBottom());
    }
    
    @Test
    public void testMapViewBitmapCopier() {
        Bitmap source = PowerMock.createMock(Bitmap.class);
        Bitmap copy = PowerMock.createMock(Bitmap.class);
        
        EasyMock.expect(source.copy(Bitmap.Config.ARGB_8888, true)).andReturn(copy);
        
        PowerMock.replayAll();
        assertEquals(copy, new MapViewBitmapCopier().copy(source));
        PowerMock.verifyAll();
    }
    
    @Test
    public void testMapViewBitmapCopierGetBottom() {
        assertEquals(3, new MapViewBitmapCopier().getBottom());
    }
    
    @Test
    public void testListViewBitmapCopierGetDrawable() throws Exception {
        BitmapDrawable drawable = PowerMock.createMock(BitmapDrawable.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        
        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(drawable);
        
        PowerMock.replayAll();
        new ListViewBitmapCopier().getDrawable(bitmap);
        PowerMock.verifyAll();
    }
    

    @Test
    public void testMapViewBitmapCopierGetDrawable() throws Exception {
        BitmapDrawable drawable = PowerMock.createMock(BitmapDrawable.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        
        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(drawable);
        EasyMock.expect(drawable.getIntrinsicWidth()).andReturn(110);
        EasyMock.expect(drawable.getIntrinsicHeight()).andReturn(220);
        drawable.setBounds(-55, -220, 55, 0);
        
        PowerMock.replayAll();
        new MapViewBitmapCopier().getDrawable(bitmap);
        PowerMock.verifyAll();
    }
    
    @Test
    public void testCreateRating3() throws Exception {
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
        new RatingsGenerator().createRating(unselected, halfSelected,
                selected, 3);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateRating1() throws Exception {
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

        halfSelected.setBounds(0, 0, 9, 4);
        halfSelected.draw(canvas);
        unselected.setBounds(10, 0, 19, 4);
        unselected.draw(canvas);
        unselected.setBounds(20, 0, 29, 4);
        unselected.draw(canvas);
        unselected.setBounds(30, 0, 39, 4);
        unselected.draw(canvas);
        unselected.setBounds(40, 0, 49, 4);
        unselected.draw(canvas);

        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(
                bitmapDrawable);

        PowerMock.replayAll();
        new RatingsGenerator().createRating(unselected, halfSelected,
                selected, 1);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetRatings() {
        Drawable drawable0 = PowerMock.createMock(Drawable.class);
        Drawable drawable1 = PowerMock.createMock(Drawable.class);
        Drawable drawable2 = PowerMock.createMock(Drawable.class);
        Drawable drawables[] = {
                drawable0, drawable1, drawable2
        };
        RatingsGenerator ratingsGenerator = PowerMock.createMock(RatingsGenerator.class);
        Drawable rating = PowerMock.createMock(Drawable.class);

        EasyMock.expect(
                ratingsGenerator.createRating(drawable0, drawable1, drawable2, 1)).andReturn(null);
        EasyMock.expect(
                ratingsGenerator.createRating(drawable0, drawable1, drawable2, 2)).andReturn(rating);

        PowerMock.replayAll();
        Drawable ratings[] = new RatingsArray(ratingsGenerator).getRatings(drawables, 2);
        assertEquals(rating, ratings[1]);
        PowerMock.verifyAll();
    }
}
