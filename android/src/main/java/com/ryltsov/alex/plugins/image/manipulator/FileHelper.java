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

    public static InputStream getInputStreamFromUriString(Context context, String uriString) throws IOException, IllegalArgumentException {
        if (uriString == null || uriString.isEmpty()) {
            throw new IllegalArgumentException("The URI string is null or empty.");
        }

        Uri uri = Uri.parse(uriString);

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Handle content:// URIs
            return context.getContentResolver().openInputStream(uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // Handle file:// URIs
            return new FileInputStream(uri.getPath());
        } else {
            // Assume it's a plain file path
            return new FileInputStream(uriString);
        }
    }

    public static InputStream getInputStream (Context context, String uriString) throws IOException {

        if (uriString == null || uriString.isEmpty()) {
            throw new IllegalArgumentException("The URI string is null or empty.");
        }

        Uri uri = Uri.parse(uriString);
        if ("content".equalsIgnoreCase(uri.getScheme())) { // if (u.getScheme().equals("content")) {
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
            // Context context = this.cordova.getActivity().getApplicationContext();
            // folder = getApplicationDirectory(this.context).
            folder = context.getDir(folderName, Context.MODE_PRIVATE);
        }

        // boolean success = true;
        if (!folder.exists()) {
            boolean success = folder.mkdir();
            if(!success) {
                throw new ImageManipulatorException("Failed to create folder to save the resized file");
            }
        }

        // if (success) {
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
            // Log.e("Protonet", e.toString());
            throw new ImageManipulatorException("Failed to save resized file. " + ex);
        }
        return Uri.fromFile(file);
        // } else {
        //     throw new ImageManipulatorException("Failed to save resized file. " + ex);
        //}
        // return null;
    }

}
