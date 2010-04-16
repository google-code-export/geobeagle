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

import com.google.inject.Inject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GraphicsGenerator {
    private final RatingsGenerator mRatingsGenerator;
    private final AttributePainter mAttributePainter;
    private final Resources mResources;

    public static class AttributePainter {
        private final Paint mTempPaint;
        private final Rect mTempRect;
        
        @Inject
        public AttributePainter(Paint tempPaint, Rect tempRect) {
            mTempPaint = tempPaint;
            mTempRect = tempRect;
        }
        
        void drawAttribute(int position, int thickness, int imageHeight, int imageWidth,
                Canvas canvas, double attribute, int color) {
            final int diffWidth = (int)(imageWidth * (attribute / 10.0));
            final int MARGIN = 1;
            final int base = imageHeight - MARGIN;
            final int attributeBottom = base - position * (thickness + 1);
            final int attributeTop = attributeBottom - thickness;
            mTempPaint.setColor(color);
            mTempRect.set(0, attributeTop, diffWidth, attributeBottom);
            canvas.drawRect(mTempRect, mTempPaint);
        }
    }
    
    @Inject
    public GraphicsGenerator(RatingsGenerator ratingsGenerator, AttributePainter attributePainter,
            Resources resources) {
        mRatingsGenerator = ratingsGenerator;
        mAttributePainter = attributePainter;
        mResources = resources;
    }

    public static class RatingsGenerator {
        Drawable createRating(Drawable unselected, Drawable halfSelected, Drawable selected,
                int rating) {
            int width = unselected.getIntrinsicWidth();
            int height = unselected.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(5 * width, 16, Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(bitmap);
            int i = 0;
            while (i < rating / 2) {
                draw(width, height, c, i++, selected);
            }
            if (rating % 2 == 1) {
                draw(width, height, c, i++, halfSelected);
            }
            while (i < 5) {
                draw(width, height, c, i++, unselected);
            }
            return new BitmapDrawable(bitmap);
        }

        private void draw(int width, int height, Canvas c, int i, Drawable drawable) {
            drawable.setBounds(width * i, 0, width * (i + 1) - 1, height - 1);
            drawable.draw(c);
        }
    }

    public Drawable[] getRatings(Drawable drawables[], int maxRatings) {
        Drawable[] ratings = new Drawable[maxRatings];
        for (int i = 1; i <= maxRatings; i++) {
            ratings[i - 1] = mRatingsGenerator.createRating(drawables[0], drawables[1],
                    drawables[2], i);
        }
        return ratings;
    }

    public Drawable createIcon(Geocache geocache) {
        return createOverlay(geocache, 3, -5, geocache.getCacheType().icon());
    }

    private Drawable createOverlay(Geocache geocache, int thickness, int bottom, int backdropId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mResources, backdropId);
        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();
        Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight() - bottom,
                Bitmap.Config.ARGB_8888);
        int[] pixels = new int[imageWidth * imageHeight];
        bitmap.getPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
        copy.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
        imageHeight = copy.getHeight();
        
        Canvas canvas = new Canvas(copy);
        mAttributePainter.drawAttribute(1, thickness, imageHeight, imageWidth, canvas, geocache
                .getDifficulty(), Color.argb(255, 0x20, 0x20, 0xFF));
        mAttributePainter.drawAttribute(0, thickness, imageHeight, imageWidth, canvas, geocache
                .getTerrain(), Color.argb(255, 0xDB, 0xA1, 0x09));
        
        return new BitmapDrawable(copy);
    }
}
