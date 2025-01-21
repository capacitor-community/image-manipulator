package com.ryltsov.alex.plugins.image.manipulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import com.getcapacitor.Bridge;
import com.getcapacitor.FileUtils;
import com.getcapacitor.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageManipulator {

    private static final String TAG = "ImageManipulator";

    private final Context context;
    private final Bridge bridge;

    ImageManipulator(Context context, Bridge bridge) {
        this.context = context;
        this.bridge = bridge;
    }

    /**
     * Method to get image dimensions (height and width)
     *
     * @param imagePath image path
     * @return image dimensions (width and height)
     */
    public ImageDimensions getDimensions(String imagePath) throws ImageManipulatorException {

        InputStream imageStream = null;

        try {
            imageStream = FileHelper.getInputStream(this.context, imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // NOTE: decode with inJustDecodeBounds=true to get dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);

            return new ImageDimensions(options.outWidth, options.outHeight);
        } catch (OutOfMemoryError ex) {
            Logger.error("ImageManipulator OutOfMemoryError exception occurred", ex);
            throw new ImageManipulatorException("Out of memory: " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.error("ImageManipulator FileNotFoundException exception occurred", ex);
            throw new ImageManipulatorException("No such image found: " + ex.getMessage());
        } catch (IOException ex) {
            Logger.error("ImageManipulator IOException exception occurred", ex);
            throw new ImageManipulatorException("Error occurred while reading the image: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.error("ImageManipulator exception thrown", ex);
            throw new ImageManipulatorException("Unexpected error occurred: " + ex.getMessage());
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    Logger.error(TAG, "UNABLE_TO_PROCESS_IMAGE", e);
                }
            }
        }
    }

    /**
     * Method to to resize image
     *
     * @param imagePath path to the image to resize
     * @param folderName directory where to save the resized image
     * @param fileName file name (without extension) to save save the resized image
     * @param quality quality (0-100) for the saved resized image
     * @param maxWidth required image width
     * @param maxHeight required image width
     * @param fixRotation fix rotation based on exif info
     *
     * @return resized image info
     */
    public ImageResizingResult resize(
            String imagePath,
            String folderName,
            String fileName,
            int quality,
            int maxWidth,
            int maxHeight,
            boolean fixRotation
    ) throws ImageManipulatorException {

        ImageDimensions dimensions = getDimensions(imagePath);

        if (
                (maxWidth == 0 || maxWidth >= dimensions.width()) &&
                (maxHeight == 0 || maxHeight >= dimensions.height())
        ) {
            Uri imageUri = Uri.parse(imagePath);
            String webPath = FileUtils.getPortablePath(context, bridge.getLocalUrl(), imageUri);
            return new ImageResizingResult(
                dimensions.width(),
                dimensions.height(),
                dimensions.width(),
                dimensions.height(),
                imagePath,
                webPath,
                false
            );
        }

        ImageResizingResultBase imageResizingResultBase = this.decodeScaledBitmapFromUri(
                imagePath,
                dimensions.width(),
                dimensions.height(),
                maxWidth,
                maxHeight
        );

        Bitmap bitmap = imageResizingResultBase.scaledBitmap();

        if (bitmap == null) {
            throw new ImageManipulatorException("Error reading the image");
        }

        if (fixRotation) {
            // NOTE: Get the exif rotation in degrees, create a transformation matrix, and rotate the bitmap
            int rotation = getRotationDegrees(getRotation(imagePath));
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotation);}
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);
        }

        Uri savedScaledFileUri = FileHelper.saveFile(this.context, bitmap, folderName, fileName, quality);
        if (savedScaledFileUri == null) {
            throw new ImageManipulatorException("Failed to save the resized image");
        }

        String webPath = FileUtils.getPortablePath(context, bridge.getLocalUrl(), savedScaledFileUri);
        return new ImageResizingResult(
                imageResizingResultBase.originalWidth(),
                imageResizingResultBase.originalHeight(),
                imageResizingResultBase.resizedWidth(),
                imageResizingResultBase.resizedHeight(),
                savedScaledFileUri.toString(),
                webPath,
                imageResizingResultBase.resized()
        );

    }

    /**
     * Method to load a Bitmap using the provided file uri path and scale it to the required height and width taking into account
     * the original image aspect ratio
     *
     * @param imagePath path to the image to resize
     * @param originalWidth required image width
     * @param originalHeight required image width
     * @param maxWidth required image width
     * @param maxHeight required image width
     *
     * @return resized image info
     **/
    private ImageResizingResultBase decodeScaledBitmapFromUri(
            String imagePath,
            int originalWidth,
            int originalHeight,
            int maxWidth,
            int maxHeight
    ) throws ImageManipulatorException {

        InputStream imageStreamForUnscaledBitmap = null;

        try {

            ImageDimensions resultingImageDimensions = getResultingImageDimensions(
                    originalWidth,
                    originalHeight,
                    maxWidth,
                    maxHeight
            );

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(
                    originalWidth,
                    originalHeight,
                    resultingImageDimensions.width(),
                    resultingImageDimensions.height()
            );
            imageStreamForUnscaledBitmap = FileHelper.getInputStream(this.context, imagePath);
            Bitmap unscaledBitmap = BitmapFactory.decodeStream(imageStreamForUnscaledBitmap, null, options);
            if (unscaledBitmap == null) {
                Logger.error("ImageManipulator image data could not be decoded");
                throw new ImageManipulatorException("Image data could not be decoded");
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, resultingImageDimensions.width(), resultingImageDimensions.height(), true);
            return new ImageResizingResultBase(scaledBitmap, originalWidth, originalHeight, resultingImageDimensions.width(), resultingImageDimensions.height(), true);
        } catch (OutOfMemoryError ex) {
            Logger.error("ImageManipulator OutOfMemoryError exception occurred", ex);
            throw new ImageManipulatorException("Out of memory: " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.error("ImageManipulator FileNotFoundException exception occurred", ex);
            throw new ImageManipulatorException("No such image found: " + ex.getMessage());
        } catch (IOException ex) {
            Logger.error("ImageManipulator IOException exception occurred", ex);
            throw new ImageManipulatorException("Error occurred while reading the image: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.error("ImageManipulatorPlugin ImageManipulatorException", ex);
            throw new ImageManipulatorException("Unexpected error occurred: " + ex.getMessage());
        } finally {
            if (imageStreamForUnscaledBitmap != null) {
                try {
                    imageStreamForUnscaledBitmap.close();
                } catch (IOException e) {
                    Logger.error(TAG, "UNABLE_TO_PROCESS_IMAGE", e);
                }
            }
        }
    }

    /**
     * Method to calculate the resulting or final width and height while keeping
     * the same aspect ratio to prevent the image to be stretched or squished
     *
     * @param originalWidth original image width
     * @param originalHeight original image height
     * @param maxWidth required image width
     * @param maxHeight required image width
     * @return resulting width and height
     */
    private ImageDimensions getResultingImageDimensions(
            int originalWidth,
            int originalHeight,
            int maxWidth,
            int maxHeight
    ) {

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        // NOTE: if required width and height are not provided we return the original bitmap
        if (finalWidth <= 0 && finalHeight <= 0) {
            finalWidth = originalWidth;
            finalHeight = originalHeight;
        }
        // NOTE: when only required width was provided
        else if (finalWidth > 0 && finalHeight <= 0) {
            finalHeight = (int) (((float)finalWidth / (float)originalWidth) * originalHeight);
        }
        // NOTE: when only required height was provided
        else if (finalWidth <= 0) {
            finalWidth = (int) (((float)finalHeight / (float)originalHeight) * originalWidth);
        }
        // NOTE: when both required width and height are provided
        else {
            float originalAspectRatio = (float) originalWidth / (float) originalHeight;
            if (maxWidth / (float) maxHeight > originalAspectRatio) {
                finalWidth = (int) (maxHeight * originalAspectRatio);
            } else {
                finalHeight = (int) (maxWidth / originalAspectRatio);
            }
        }

        return new ImageDimensions(finalWidth, finalHeight);
    }

    /**
     * Method to calculate the inSampleSize value for the BitmapFactory.Options to load the image with based
     * on the required width and height
     *
     * @param originalWidth original image width
     * @param originalHeight original image height
     * @param maxWidth required image width
     * @param maxHeight required image width
     * @return inSampleSize value
     */
    private int calculateInSampleSize(
            int originalWidth,
            int originalHeight,
            int maxWidth,
            int maxHeight
    ) {
        final float originalAspectRatio = (float) originalWidth / (float) originalHeight;
        final float requiredAspectRatio = (float) maxWidth / (float) maxHeight;

        if (originalAspectRatio > requiredAspectRatio) {
            return originalWidth / maxWidth;
        } else {
            return originalHeight / maxHeight;
        }
    }

    /**
     * Gets the image rotation from the image EXIF Data
     *
     * @param exifOrientation ExifInterface.ORIENTATION_* representation of the rotation
     * @return the rotation in degrees
     */
    private int getRotationDegrees(
            int exifOrientation
    ) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    /**
     * Gets the image rotation from the image EXIF Data
     *
     * @param imageUri the URI of the image to get the rotation for
     * @return ExifInterface.ORIENTATION_* representation of the rotation
     */
    private int getRotation(
            String imageUri
    ) {
        try {
            ExifInterface exif = new ExifInterface(imageUri);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }

}
