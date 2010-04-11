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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GraphicsGenerator {
    private final RatingsGenerator mRatingsGenerator;

    @Inject
    public GraphicsGenerator(RatingsGenerator ratingsGenerator) {
        mRatingsGenerator = ratingsGenerator;
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
}
