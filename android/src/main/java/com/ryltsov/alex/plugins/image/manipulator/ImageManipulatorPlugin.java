package com.ryltsov.alex.plugins.image.manipulator;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ImageManipulator")
public class ImageManipulatorPlugin extends Plugin {

    private ImageManipulator implementation;

    @Override
    public void load() {
        implementation = new ImageManipulator(getContext(), getBridge());
    }

    @PluginMethod
    public void getDimensions(PluginCall call) {
        String imagePath = call.getString("imagePath");

        if (imagePath == null || imagePath.isEmpty()) {
            call.reject("The imagePath param is null or empty.");
            return;
        }

        try {
            ImageDimensions dimensions = implementation.getDimensions(imagePath);
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
        String imagePath = call.getString("imagePath");
        if (imagePath == null || imagePath.isEmpty()) {
            call.reject("The imagePath param is null or empty.");
            return;
        }

        String folderName = call.getString("folderName", "ResizedImages");
        String fileName = call.getString("fileName");
        int quality = call.getInt("quality", 85);
        int maxWidth = call.getInt("maxWidth", 0);
        int maxHeight = call.getInt("maxHeight", 0);
        if (maxWidth <= 0 && maxHeight <= 0) {
            call.reject("Either maxWidth or maxHeight param must be provided and be greater then 0.");
            return;
        }
        boolean fixRotation = call.getBoolean("fixRotation", false); // false;

        try {
            ImageResizingResult result = implementation.resize(imagePath, folderName, fileName, quality, maxWidth, maxHeight, fixRotation);
            JSObject ret = new JSObject();
            ret.put("originalWidth", result.originalWidth());
            ret.put("originalHeight", result.originalHeight());
            ret.put("resizedWidth", result.resizedWidth());
            ret.put("resizedHeight", result.resizedHeight());
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
