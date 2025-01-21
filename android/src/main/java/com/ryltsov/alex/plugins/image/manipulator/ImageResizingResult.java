package com.ryltsov.alex.plugins.image.manipulator;

public record ImageResizingResult(
        int originalWidth,
        int originalHeight,
        int resizedWidth,
        int maxHeight,
        String imagePath,
        String webPath,
        boolean resized
) {
}

