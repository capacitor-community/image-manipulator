package com.ryltsov.alex.plugins.image.manipulator;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

@CapacitorPlugin(name = "ImageManipulator")
public class ImageManipulatorPlugin extends Plugin {

    private static final String TAG = "ImageManipulatorPlugin";

    // private ImageManipulator implementation = new ImageManipulator();
    /*
    private final ImageManipulator implementation = new ImageManipulator(
            getContext(),
            getBridge()
    );
    */
    private ImageManipulator implementation;

    @Override
    public void load() {
        implementation = new ImageManipulator(getContext(), getBridge());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void getDimension(PluginCall call) {
        String imageUri = call.getString("imageUri");

        // TODO: add check that imageUri is defined

        try {
            ImageDimensions dimensions = implementation.getDimensions(imageUri);
            JSObject ret = new JSObject();
            ret.put("width", dimensions.width());
            ret.put("height", dimensions.height());
            call.resolve(ret);
        } catch (ImageManipulatorException ex) {
            call.reject(ex.toString());
        } catch (Exception ex) {
            call.reject("An error occurred: " + ex.getMessage());
        }
    }

    @PluginMethod
    public void resize(PluginCall call) {

        String imageUri = call.getString("imageUri");
        // TODO: add check that imageUri is defined

        String folderName = "BOSS811ResizedImages";
        String fileName = "resized";
        int quality = 85;
        int requiredWidth = 100;
        int requiredHeight = 200;
        boolean fixRotation = false;

        try {
            ImageResizingResult result = implementation.resize(
                    imageUri,
                    folderName,
                    fileName,
                    quality,
                    requiredWidth,
                    requiredHeight,
                    fixRotation
            );
            JSObject ret = new JSObject();
            ret.put("originalWidth", result.originalWidth());
            ret.put("originalHeight", result.originalHeight());
            ret.put("finalWidth", result.finalWidth());
            ret.put("finalHeight", result.finalHeight());
            ret.put("imagePath", result.imagePath());
            ret.put("webPath", result.webPath());
            ret.put("resized", result.resized());
            call.resolve(ret);
        } catch (ImageManipulatorException ex) {
            call.reject(ex.toString());
        } catch (Exception ex) {
            call.reject("An error occurred: " + ex.getMessage());
        }

    }

}
