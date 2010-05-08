package com.google.code.geobeagle;

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
    private final IconRenderer mIconRenderer;

    public GraphicsGenerator(RatingsGenerator ratingsGenerator, IconRenderer iconRenderer) {
        mRatingsGenerator = ratingsGenerator;
        mIconRenderer = iconRenderer;
    }

    public static class RatingsGenerator {
        public Drawable createRating(Drawable unselected, Drawable halfSelected, Drawable selected,
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

    static interface BitmapCopier {
        Bitmap copy(Bitmap source);
        int getBottom();
    }

    static class ListViewBitmapCopier implements BitmapCopier {
        public Bitmap copy(Bitmap source) {
            int imageHeight = source.getHeight();
            int imageWidth = source.getWidth();

            Bitmap copy = Bitmap.createBitmap(imageWidth, imageHeight + 5,
                    Bitmap.Config.ARGB_8888);
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

        public IconRenderer(AttributePainter attributePainter) {
            mAttributePainter = attributePainter;
        }

        Drawable createOverlay(Geocache geocache, int backdropId, Drawable overlayIcon,
                Resources resources, BitmapCopier bitmapCopier) {
            Bitmap bitmap = BitmapFactory.decodeResource(resources, backdropId);

            Bitmap copy = bitmapCopier.copy(bitmap);
            int imageHeight = copy.getHeight();
            int imageWidth = copy.getWidth();
            int bottom = bitmapCopier.getBottom();
            Canvas canvas = new Canvas(copy);
            mAttributePainter.drawAttribute(1, bottom, imageHeight, imageWidth, canvas, geocache
                    .getDifficulty(), Color.argb(255, 0x20, 0x20, 0xFF));
            mAttributePainter.drawAttribute(0, bottom, imageHeight, imageWidth, canvas, geocache
                    .getTerrain(), Color.argb(255, 0xDB, 0xA1, 0x09));

            drawOverlay(overlayIcon, imageWidth, canvas);

            return new BitmapDrawable(copy);
        }

        private void drawOverlay(Drawable overlayIcon, int imageWidth, Canvas canvas) {
            if (overlayIcon != null) {
                overlayIcon.setBounds(imageWidth-1-overlayIcon.getIntrinsicWidth(),
                        0, imageWidth-1, overlayIcon.getIntrinsicHeight()-1);
                overlayIcon.draw(canvas);
            }
        }
    }
    
    public Drawable createIconListView(Geocache geocache, Drawable overlayIcon, Resources resources) {
        return mIconRenderer.createOverlay(geocache, geocache.getCacheType().icon(), overlayIcon,
                resources, new ListViewBitmapCopier());
    }
    
    public Drawable createIconMapView(Geocache geocache, Drawable overlayIcon, Resources resources) {
        Drawable iconMap = mIconRenderer.createOverlay(geocache, geocache.getCacheType().iconMap(), overlayIcon, resources,
                new MapViewBitmapCopier());
        int width = iconMap.getIntrinsicWidth();
        int height = iconMap.getIntrinsicHeight();
        iconMap.setBounds(-width / 2, -height, width / 2, 0);
        return iconMap;
    }

    public static class AttributePainter {
        private final Paint mTempPaint;
        private final Rect mTempRect;
        
        public AttributePainter(Paint tempPaint, Rect tempRect) {
            mTempPaint = tempPaint;
            mTempRect = tempRect;
        }

        void drawAttribute(int position, int bottom, int imageHeight, int imageWidth,
                Canvas canvas, double attribute, int color) {
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
    
    /** Returns a new Drawable that is 'top' over 'bottom'. 
     * Top is assumed to be smaller and is centered over bottom. */
    public Drawable superimpose(Drawable top, Drawable bottom) {
        int width = bottom.getIntrinsicWidth();
        int height = bottom.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        bottom.setBounds(0, 0, width-1, height-1);
        int topWidth = top.getIntrinsicWidth();
        int topHeight = top.getIntrinsicHeight();
        top.setBounds(width/2 - topWidth/2, height/2 - topHeight/2, 
                width/2 - topWidth/2 + topWidth, height/2 - topHeight/2 + topHeight);
        bottom.draw(c);
        top.draw(c);
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        bd.setBounds(-width/2, -height, width/2, 0);  //Necessary!
        return bd;
    }
    
}
