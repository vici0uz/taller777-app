package com.odoo.addons.workshop.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

/**
 * Created by alan on 12/02/17.
 */

public class ImageTransformation {
    public static Transformation getTransformation(final ImageView imageView) {
        return new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = imageView.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                System.out.println("ALAN DEBUG: targetWidth "+ targetWidth);
                System.out.println("ALAN DEBUG: targetHeight "+ targetHeight);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }
}
