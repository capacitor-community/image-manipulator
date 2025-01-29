package com.ryltsov.alex.plugins.image.manipulator;

public record ImageResizingResult(
        int originalWidth,
        int originalHeight,
        int resizedWidth,
        int resizedHeight,
        String imagePath,
        String webPath,
        boolean resized
) {
}

