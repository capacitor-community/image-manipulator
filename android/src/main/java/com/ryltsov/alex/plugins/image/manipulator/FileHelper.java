package com.ryltsov.alex.plugins.image.manipulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHelper {

    public static InputStream getInputStream (Context context, String uriString) throws IOException {

        if (uriString == null || uriString.isEmpty()) {
            throw new IllegalArgumentException("The URI string is null or empty.");
        }

        Uri uri = Uri.parse(uriString);
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return context.getContentResolver().openInputStream(uri);
        } else {
            return new FileInputStream(new File(uri.getPath()));
        }
    }

    public static Uri saveFile(
            Context context,
            Bitmap bitmap,
            String folderName,
            String fileName,
            int quality
    ) throws ImageManipulatorException {
        File folder = null;
        if (folderName.contains("/")) {
            folder = new File(folderName.replace("file://", ""));
        } else {
            folder = new File(context.getFilesDir(), folderName);
        }

        if (!folder.exists()) {
            boolean success = folder.mkdir();
            if (!success) {
                throw new ImageManipulatorException("Failed to create folder to save the resized file");
            }
        }

        if (fileName == null) {
            fileName = System.currentTimeMillis() + ".jpg";
        } else {
            if (!fileName.endsWith(".jpg")) {
                fileName = fileName + ".jpg";
            }
        }
        File file = new File(folder, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new ImageManipulatorException("Failed to save resized file. " + ex);
        }
        return Uri.fromFile(file);

    }

}
