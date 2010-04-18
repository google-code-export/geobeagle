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
    public static class AttributePainter {
        private final Paint mTempPaint;
        private final Rect mTempRect;
        
        @Inject
        public AttributePainter(Paint tempPaint, Rect tempRect) {
            mTempPaint = tempPaint;
            mTempRect = tempRect;
        }

        void drawAttribute(int position, int bottom, int imageHeight, int imageWidth, Canvas canvas,
                double attribute, int color) {
            final int diffWidth = (int)(imageWidth * (attribute / 10.0));
            final int MARGIN = 1;
            final int THICKNESS = 3;
            final int base = imageHeight - bottom - MARGIN;
            final int attributeBottom = base - position * (THICKNESS + 1);
            final int attributeTop = attributeBottom - THICKNESS;
            mTempPaint.setColor(color);
            mTempRect.set(0, attributeTop, diffWidth, attributeBottom);
            canvas.drawRect(mTempRect, mTempPaint);
        }
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

        void draw(int width, int height, Canvas c, int i, Drawable drawable) {
            drawable.setBounds(width * i, 0, width * (i + 1) - 1, height - 1);
            drawable.draw(c);
        }
        
    }
    
    public static class RatingsArray {
        private final RatingsGenerator mRatingsGenerator;

        @Inject
        RatingsArray(RatingsGenerator ratingsGenerator) {
            mRatingsGenerator = ratingsGenerator;
        }

        public Drawable[] getRatings(Drawable[] drawables, int maxRatings) {
            Drawable[] ratings = new Drawable[maxRatings];
            for (int i = 1; i <= maxRatings; i++) {
                ratings[i - 1] = mRatingsGenerator.createRating(drawables[0], drawables[1],
                        drawables[2], i);
            }
            return ratings;
        }
    }

    public interface BitmapCopier {
        Bitmap copy(Bitmap source);
        int getBottom();
    }

    public static class ListViewBitmapCopier implements BitmapCopier {
        public Bitmap copy(Bitmap source) {
            int imageHeight = source.getHeight();
            int imageWidth = source.getWidth();

            Bitmap copy = Bitmap.createBitmap(imageWidth, imageHeight + 5, Bitmap.Config.ARGB_8888);
            int[] pixels = new int[imageWidth * imageHeight];
            source.getPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
            copy.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
            return copy;
        }
        
        public int getBottom() {
            return 0;
        }
    }
    
    public static class MapViewBitmapCopier implements BitmapCopier {
        public Bitmap copy(Bitmap source) {
            return source.copy(Bitmap.Config.ARGB_8888, true);
        }

        public int getBottom() {
            return 3;
        }
    }

    public static class IconRenderer {
        private final AttributePainter mAttributePainter;
        private final Resources mResources;
        
        @Inject
        public IconRenderer(AttributePainter attributePainter, Resources resources) {
            mAttributePainter = attributePainter;
            mResources = resources;
        }

        Drawable renderIcon(Geocache geocache, int backdropId,
                BitmapCopier listViewBitmapCopier) {
            Bitmap bitmap = BitmapFactory.decodeResource(mResources, backdropId);

            Bitmap copy = listViewBitmapCopier.copy(bitmap);
            int imageHeight = copy.getHeight();
            int imageWidth = copy.getWidth();

            Canvas canvas = new Canvas(copy);
            mAttributePainter.drawAttribute(1, 0, imageHeight, imageWidth, canvas, geocache
                    .getDifficulty(), Color.rgb(0x20, 0x20, 0xFF));
            mAttributePainter.drawAttribute(0, 0, imageHeight, imageWidth, canvas, geocache
                    .getTerrain(), Color.rgb(0xDB, 0xA1, 0x09));

            return new BitmapDrawable(copy);
        }
    }

    public static class IconFactory {
        private final IconRenderer mIconRenderer;
        private final ListViewBitmapCopier mListViewBitmapCopier;

        @Inject
        public IconFactory(IconRenderer iconRenderer, ListViewBitmapCopier listViewBitmapCopier) {
            mIconRenderer = iconRenderer;
            mListViewBitmapCopier = listViewBitmapCopier;
        }

        public Drawable createListViewIcon(Geocache geocache) {
            return mIconRenderer.renderIcon(geocache, geocache.getCacheType().icon(),
                    mListViewBitmapCopier);
        }

        public Drawable createMapViewIcon(Geocache geocache, MapViewBitmapCopier mapViewBitmapCopier) {
            Drawable iconMap = mIconRenderer.renderIcon(geocache, geocache.getCacheType().iconMap(),
                    mapViewBitmapCopier);
            int width = iconMap.getIntrinsicWidth();
            int height = iconMap.getIntrinsicHeight();
            iconMap.setBounds(-width / 2, -height, width / 2, 0);
            return iconMap;
        }
        
    }
}
