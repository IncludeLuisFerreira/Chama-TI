package com.example.chamati.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static String compressImage(String imagePath, int maxDimension, int quality) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int width = options.outWidth;
        int height = options.outHeight;

        int scale = 1;
        while (width / scale > maxDimension || height / scale > maxDimension) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        Bitmap original = BitmapFactory.decodeFile(imagePath, options);

        if (original == null) return imagePath;

        float aspectRatio = (float) original.getWidth() / (float) original.getHeight();
        int newWidth, newHeight;
        if (original.getWidth() > original.getHeight()) {
            newWidth = Math.min(original.getWidth(), maxDimension);
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            newHeight = Math.min(original.getHeight(), maxDimension);
            newWidth = Math.round(newHeight * aspectRatio);
        }

        Bitmap scaled = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);

        File file = new File(imagePath);
        try (FileOutputStream out = new FileOutputStream(file)) {
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            e.printStackTrace();
            return imagePath;
        } finally {
            if (original != scaled) {
                original.recycle();
            }
            scaled.recycle();
        }

        return imagePath;
    }
}
