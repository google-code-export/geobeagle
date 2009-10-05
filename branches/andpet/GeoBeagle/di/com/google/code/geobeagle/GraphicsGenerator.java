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

    public static Drawable createRating(Drawable unselected, Drawable halfSelected,
            Drawable selected, int rating) {
        int width = unselected.getIntrinsicWidth();
        int height = unselected.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(5*width, 16, Bitmap.Config.ARGB_8888);
        
        Canvas c = new Canvas(bitmap);
        for (int i = 0; i < rating / 2; i++) {
            selected.setBounds(width*i, 0, width*(i+1)-1, height-1);
            selected.draw(c);
        }
        if (rating % 2 == 1) {
            int i = rating / 2;
            halfSelected.setBounds(width*i, 0, width*(i+1)-1, height-1);
            halfSelected.draw(c);
        }
        for (int i = rating / 2 + (rating % 2); i < 5; i++) {
            unselected.setBounds(width*i, 0, width*(i+1)-1, height-1);
            unselected.draw(c);
        }
        return new BitmapDrawable(bitmap);
    }
    
    public static Drawable[] getDifficultyRatings(Resources r) {
        Drawable[] ratings = new Drawable[9];
        for (int i = 1; i < 10; i++) {
            ratings[i-1] = createRating(r.getDrawable(R.drawable.ribbon_unselected_dark),
                    r.getDrawable(R.drawable.ribbon_half_bright),
                    r.getDrawable(R.drawable.ribbon_selected_bright), i);
        }
        return ratings;
    }

    public static Drawable[] getTerrainRatings(Resources r) {
        Drawable[] ratings = new Drawable[9];
        for (int i = 1; i < 10; i++) {
            ratings[i-1] = createRating(r.getDrawable(R.drawable.paw_unselected_dark),
                    r.getDrawable(R.drawable.paw_half_light),
                    r.getDrawable(R.drawable.paw_selected_light), i);
        }
        return ratings;
    }
    
    private static Paint mTempPaint = new Paint();
    private static Rect mTempRect = new Rect();
    private static Drawable createOverlay(Geocache geocache, int thickness, int bottom, 
            int backdropId, Resources resources) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, backdropId);
        
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);

        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();

        mTempPaint.setColor(Color.RED);
        int diffHeight = (int)((imageHeight - bottom - 1) * (geocache.getDifficulty()/10.0));
        mTempRect.set(1, imageHeight-1-diffHeight-bottom, thickness+1, imageHeight-1-bottom);
        canvas.drawRect(mTempRect, mTempPaint);

        mTempPaint.setARGB(255, 0xDB, 0xA1, 0x09);  //a lighter brown
        //mTempPaint.setARGB(255, 139, 94, 23);  //same color as paws
        int terrHeight = (int)((imageHeight - bottom - 1) * (geocache.getTerrain()/10.0));
        mTempRect.set(imageWidth-thickness-1, imageHeight-1-terrHeight-bottom, 
                imageWidth-1, imageHeight-1-bottom);
        canvas.drawRect(mTempRect, mTempPaint);

        return new BitmapDrawable(copy);
    }

    public static Drawable createIcon(Geocache geocache, Resources resources) {
        return createOverlay(geocache, 3, 1, geocache.getCacheType().icon(), 
                resources);
    }
    
    public static Drawable createIconMap(Geocache geocache, Resources resources) {
        Drawable iconMap = createOverlay(geocache, 3, 1, 
                geocache.getCacheType().iconMap(), resources);
        int width = iconMap.getIntrinsicWidth();
        int height = iconMap.getIntrinsicHeight();
        iconMap.setBounds(-width/2, -height, width/2, 0);
        return iconMap;
    }
    
}
