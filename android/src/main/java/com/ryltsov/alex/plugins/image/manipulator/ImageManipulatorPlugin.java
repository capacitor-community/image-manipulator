package com.ryltsov.alex.plugins.image.manipulator;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ImageManipulator")
public class ImageManipulatorPlugin extends Plugin {

    private final ImageManipulator implementation = new ImageManipulator(
            getContext(),
            getBridge()
    );

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    /*
    @PluginMethod
    public void resize(PluginCall call) {
        String imageUri = call.getString("imageUri");
        String folderName = "BOSS811ResizedImages";
        String fileName = "resized";
        int quality = 85;
        int requiredWidth = 100;
        int requiredHeight = 200;
        boolean fixRotation = true;

        // Context context = getContext(); // Get the Android Context
        // getActivity().getApplicationContext().getDir(folderName, context.MODE_PRIVATE);
        // val appFolder = context.filesDir.absolutePath;

        try {
            ImageResizingResult result = implementation.resize(imageUri, folderName, fileName, quality, requiredWidth, requiredHeight, fixRotation);
            JSObject ret = new JSObject();
            ret.put("originalWidth", result.originalWidth());
            ret.put("originalHeight", result.originalHeight());
            ret.put("finalWidth", result.finalWidth());
            ret.put("finalHeight", result.finalHeight());
            ret.put("imagePath", result.imagePath());
            ret.put("webPath", result.webPath());
            ret.put("resized", result.resized());
            call.resolve(ret);
        } catch (ImageManipulatorException e) {
            // throw new RuntimeException(e);
            call.reject(e.toString());
        }

    }
    */

}
