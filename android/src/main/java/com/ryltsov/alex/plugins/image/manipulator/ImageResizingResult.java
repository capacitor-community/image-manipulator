package com.ryltsov.alex.plugins.image.manipulator;

import android.graphics.Bitmap;

public record ImageResizingResult(
        int originalWidth,
        int originalHeight,
        int finalWidth,
        int finalHeight,
        String imagePath,
        String webPath,
        boolean resized
) {
}

