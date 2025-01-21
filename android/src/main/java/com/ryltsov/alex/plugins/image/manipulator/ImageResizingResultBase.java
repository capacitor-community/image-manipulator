package com.ryltsov.alex.plugins.image.manipulator;

import android.graphics.Bitmap;

public record ImageResizingResultBase(
        Bitmap scaledBitmap,
        int originalWidth,
        int originalHeight,
        int resizedWidth,
        int resizedHeight,
        boolean resized
) {
}
