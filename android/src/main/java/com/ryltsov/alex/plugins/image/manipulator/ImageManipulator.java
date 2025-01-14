package com.ryltsov.alex.plugins.image.manipulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.getcapacitor.Bridge;
import com.getcapacitor.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageManipulator {

    private final Context context;
    private final Bridge bridge;
    ImageManipulator(Context context, Bridge bridge) {
        this.context = context;
        this.bridge = bridge;
    }

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public ImageResizingResult resize(
            String imageUri,
            String folderName,
            String fileName,
            int quality,
            int requiredWidth,
            int requiredHeight,
            boolean fixRotation
    ) throws ImageManipulatorException {

        ImageResizingResultBase imageResizingResultBase = this.decodeScaledBitmapFromUri(imageUri, requiredWidth, requiredHeight);

        Bitmap bitmap = imageResizingResultBase.scaledBitmap();

        if (bitmap == null) {
            // Log.e("Protonet", "There was an error reading the image");
            // callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
            // return false;
            throw new ImageManipulatorException("There was an error reading the image");
        }

        if (fixRotation) {
            // NOTE Get the exif rotation in degrees, create a transformation matrix, and rotate the bitmap
            int rotation = getRotationDegrees(getRotation(imageUri));
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

        Uri savedScaledFileUri = saveFile(bitmap, folderName, fileName, quality);
        assert savedScaledFileUri != null;

        String webPath = FileUtils.getPortablePath(context, bridge.getLocalUrl(), savedScaledFileUri);
        ImageResizingResult result = new ImageResizingResult(
                imageResizingResultBase.originalWidth(),
                imageResizingResultBase.originalHeight(),
                imageResizingResultBase.finalWidth(),
                imageResizingResultBase.finalHeight(),
                savedScaledFileUri.toString(),
                webPath,
                imageResizingResultBase.resized()
        );

        return result; // savedScaledFileUri.toString();
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

    /**
     * Method to load a Bitmap using the provided file uri path
     *
     * @params image file URI
     **/
    private ImageResizingResultBase decodeScaledBitmapFromUri(
            String imageUri,
            int requiredWidth,
            int requiredHeight
    ) {
            // NOTE: First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageUri, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            ImageDimensions resultingImageDimensions = getResultingImageDimensions(
                    originalWidth,
                    originalHeight,
                    requiredWidth,
                    requiredHeight
            );

            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(
                    originalWidth,
                    originalHeight,
                    requiredWidth,
                    requiredHeight
            );
            Bitmap unscaledBitmap = BitmapFactory.decodeFile(imageUri, options);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, resultingImageDimensions.width(), resultingImageDimensions.height(), true);
            return new ImageResizingResultBase(scaledBitmap, originalWidth, originalHeight, resultingImageDimensions.width(), resultingImageDimensions.height(), true);
            // options.inJustDecodeBounds = false;
            // options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, width, height);
            // Bitmap unscaledBitmap = BitmapFactory.decodeStream(FileHelper.getInputStreamFromUriString(uriString, cordova), null, options);
            // return Bitmap.createScaledBitmap(unscaledBitmap, retval[0], retval[1], true);

        // return null;
    }

    /**
     * Method to calculate the inSampleSize value for the BitmapFactory.Options to load the image with based
     * on the required width and height
     *
     * @param originalWidth original image width
     * @param originalHeight original image height
     * @param requiredWidth required image width
     * @param requiredHeight required image width
     * @return inSampleSize value
     */
    private int calculateInSampleSize(
            int originalWidth,
            int originalHeight,
            int requiredWidth,
            int requiredHeight
    ) {
        final float originalAspectRatio = (float) originalWidth / (float) originalHeight;
        final float requiredAspectRatio = (float) requiredWidth / (float) requiredHeight;

        if (originalAspectRatio > requiredAspectRatio) {
            return originalWidth / requiredWidth;
        } else {
            return originalHeight / requiredHeight;
        }
    }
    public static int calculateInSampleSize2(
            int originalWidth,
            int originalHeight,
            int requiredWidth,
            int requiredHeight
    ) {

        int inSampleSize = 1;

        if (originalHeight > requiredHeight || originalWidth > requiredWidth) {
            final int halfHeight = originalHeight / 2;
            final int halfWidth = originalWidth / 2;

            // NOTE: Calculate the largest inSampleSize value that is a power of 2 and keeps both height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= requiredHeight && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * Method to calculate the resulting or final width and height while keeping
     * the same aspect ratio to prevent the image to be stretched or squished
     *
     * @param originalWidth original image width
     * @param originalHeight original image height
     * @param requiredWidth required image width
     * @param requiredHeight required image width
     * @return resulting width and height
     */
    private ImageDimensions getResultingImageDimensions(
            int originalWidth,
            int originalHeight,
            int requiredWidth,
            int requiredHeight
    ) {
        //
        int finalWidth = requiredWidth;
        int finalHeight = requiredHeight;

        // NOTE: if required width and height are not provided we return the original bitmap
        if (finalWidth <= 0 && finalHeight <= 0) {
            finalWidth = originalWidth;
            finalHeight = originalHeight;
        }
        // NOTE: when only required width was provided
        else if (finalWidth > 0 && finalHeight <= 0) {
            finalHeight = (finalWidth / originalWidth) * originalHeight; // (newWidth * origHeight) / origWidth;
        }
        // NOTE: when only required height was provided
        else if (finalWidth <= 0) {
            finalWidth = (finalHeight / originalHeight) * originalWidth; // (finalHeight * originalWidth) / originalHeight;
        }
        // NOTE: when both required width and height are provided
        else {
            float originalAspectRatio = (float) originalWidth / (float) originalHeight;
            if (requiredWidth / (float) requiredHeight > originalAspectRatio) {
                finalWidth = (int) (requiredHeight * originalAspectRatio);
            } else {
                finalHeight = (int) (requiredWidth / originalAspectRatio);
            }
        }
        // int[] retval = new int[2];
        // retval[0] = finalWidth;
        // retval[1] = finalHeight;
        // return retval;

        return new ImageDimensions(finalWidth, finalHeight);
    }

    /*
    public String getAppFolder(Context context) {
        File appFolder = context.getFilesDir(); // Internal app storage
        return appFolder.getAbsolutePath();
    }
    */

    /*
    public String getApplicationDirectory(Context context) {
        File appDir = context.getFilesDir(); // For internal storage
        return appDir.getAbsolutePath();
    }
    */

    private Uri saveFile(
            Bitmap bitmap,
            String folderName,
            String fileName,
            int quality
    ) throws ImageManipulatorException {
        File folder = null;
        if (folderName.contains("/")) {
            folder = new File(folderName.replace("file://", ""));
        } else {
            // Context context = this.cordova.getActivity().getApplicationContext();
            // folder = getApplicationDirectory(this.context).
            folder = context.getDir(folderName, Context.MODE_PRIVATE);

        }

        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success) {
            if (fileName == null) {
                fileName = System.currentTimeMillis() + ".jpg";
            }
            File file = new File(folder, fileName);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                out.flush();
                out.close();
            } catch (Exception e) {
               // Log.e("Protonet", e.toString());
                throw new ImageManipulatorException("Failed to save resized file. " + e);
            }
            return Uri.fromFile(file);
        }
        return null;
    }

    /*
    // scale and keep aspect ratio
    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    // scale and keep aspect ratio
    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }


    // scale and keep aspect ratio
    public static Bitmap scaleToFill(Bitmap b, int width, int height)
    {
        float factorH = height / (float) b.getWidth();
        float factorW = width / (float) b.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse),
                (int) (b.getHeight() * factorToUse), true);
    }

    */

}
