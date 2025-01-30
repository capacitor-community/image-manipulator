import Capacitor
import Foundation

/// Please read the Capacitor iOS Plugin Development Guide
/// here: https://capacitorjs.com/docs/plugins/ios
@objc(ImageManipulatorPlugin)
public class ImageManipulatorPlugin: CAPPlugin, CAPBridgedPlugin {

    public let identifier = "ImageManipulatorPlugin"
    public let jsName = "ImageManipulator"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getDimensions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "resize", returnType: CAPPluginReturnPromise),
    ]

    // NOTE: Error message constants
    static let FAILED_TO_LOAD_IMAGE_ERROR = "Failed to load image"
    static let FAILED_TO_CREATE_IMAGE_DATA_ERROR = "Failed to create image data"
    static let FAILED_TO_SAVE_RESIZED_IMAGE_ERROR = "Failed to save resized image"
    static let FAILED_TO_GET_RESIZED_JPEG_IMAGE_FROM_DATA_ERROR = "Failed to get resized JPEG image from data"

    private var implementation: ImageManipulator?
    override public func load() {
        guard let bridge = bridge else { return }
        implementation = ImageManipulator(bridge: bridge)
    }

    @objc func getDimensions(_ call: CAPPluginCall) {

        guard let imagePath = call.options["imagePath"] as? String else {
            call.reject("Must provide an imagePath")
            return
        }

        guard let implementation = implementation else {
            call.reject("Failed to initialize plugin")
            return
        }
        
        do {
            let dimensions: ImageDimensions = try implementation.getDimensions(imagePath: imagePath)
            call.resolve([
                "width": dimensions.width,
                "height": dimensions.height,
            ])
        } catch ImageManipulatorError.failedToLoadImage {
            call.reject(ImageManipulatorPlugin.FAILED_TO_LOAD_IMAGE_ERROR)
        } catch ImageManipulatorError.failedToCreateImageData {
            call.reject(ImageManipulatorPlugin.FAILED_TO_CREATE_IMAGE_DATA_ERROR)
        } catch ImageManipulatorError.failedToSaveResizedImage {
            call.reject(ImageManipulatorPlugin.FAILED_TO_SAVE_RESIZED_IMAGE_ERROR)
        } catch ImageManipulatorError.failedToGetResizedJPEGImageFromData {
            call.reject(ImageManipulatorPlugin.FAILED_TO_GET_RESIZED_JPEG_IMAGE_FROM_DATA_ERROR)
        } catch {
            call.reject(error.localizedDescription, nil, error)
        }
    }

    @objc func resize(_ call: CAPPluginCall) {

        guard let imagePath = call.options["imagePath"] as? String else {
            call.reject("Must provide an imagePath")
            return
        }
        let fileName = call.getString("fileName")
        let quality = call.getInt("quality", 85)
        let maxWidth = call.getInt("maxWidth", 0)
        let maxHeight = call.getInt("maxHeight", 0)
        if (maxWidth <= 0 && maxHeight <= 0) {
            call.reject("Either maxWidth or maxHeight param must be provided and be greater then 0.");
            return;
        }
        let fixRotation = call.getBool("fixRotation", false)

        guard let implementation = implementation else {
            call.reject("Failed to initialize plugin")
            return
        }
        
        do {
            let imageResizingResult: ImageResizingResult = try implementation.resize(
                imagePath: imagePath, fileName: fileName, quality: quality,
                maxWidth: maxWidth, maxHeight: maxHeight, fixRotation: fixRotation
            )
            
            var result: [String: Any] = [
                "originalWidth": imageResizingResult.originalWidth,
                "originalHeight": imageResizingResult.originalHeight,
                "resizedWidth": imageResizingResult.resizedWidth,
                "resizedHeight": imageResizingResult.resizedHeight,
                "imagePath": imageResizingResult.imagePath,
                "resized": imageResizingResult.resized
            ]
            if (imageResizingResult.webPath != nil && !imageResizingResult.webPath!.isEmpty) {
                result["webPath"] = imageResizingResult.webPath
            }
            call.resolve(result)
        } catch ImageManipulatorError.failedToLoadImage {
            call.reject(ImageManipulatorPlugin.FAILED_TO_LOAD_IMAGE_ERROR)
        } catch ImageManipulatorError.failedToCreateImageData {
            call.reject(ImageManipulatorPlugin.FAILED_TO_CREATE_IMAGE_DATA_ERROR)
        } catch ImageManipulatorError.failedToSaveResizedImage {
            call.reject(ImageManipulatorPlugin.FAILED_TO_SAVE_RESIZED_IMAGE_ERROR)
        } catch ImageManipulatorError.failedToGetResizedJPEGImageFromData {
            call.reject(ImageManipulatorPlugin.FAILED_TO_GET_RESIZED_JPEG_IMAGE_FROM_DATA_ERROR)
        } catch {
            call.reject(error.localizedDescription, nil, error)
        }

    }

}
