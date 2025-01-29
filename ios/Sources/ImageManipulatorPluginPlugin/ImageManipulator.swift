import Capacitor
import Foundation

@objc public class ImageManipulator: NSObject {
    
    /*
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
    */
    
    // private final Context context;
    // private var Bridge bridge;

    // ImageManipulator(bridge) {
    //     this.bridge = bridge;
    // }
    private var bridge: CAPBridgeProtocol

    init(bridge: CAPBridgeProtocol) {
        self.bridge = bridge
    }
    
    func getImage(imagePath: String) throws -> UIImage {
        
        var sourceImage: UIImage?
        
        // Load image
        if FileManager.default.fileExists(atPath: imagePath) {
            sourceImage = UIImage(contentsOfFile: imagePath)
        } else if let url = URL(string: imagePath), let data = try? Data(contentsOf: url) {
            sourceImage = UIImage(data: data)
        }

        guard let sourceImage = sourceImage else {
            print("Failed to load image at path: \(imagePath)")
            throw ImageManipulatorError.failedToLoadImage
        }
        
        return sourceImage
    }
    
    func getDimensions(imagePath: String) throws -> ImageDimensions {

        let image = try getImage(imagePath: imagePath)
        
        // Get the width and height of the image
        // let width = Int(image.size.width)
        // let height = Int(image.size.height)
        
        // return (width, height)
        
        let imageDimensions = ImageDimensions(width: Int(image.size.width), height: Int(image.size.height))
        return imageDimensions
    }
    
    func resize(
        imagePath: String, fileName: String?, quality: Int,
        maxWidth: Int, maxHeight: Int, fixRotation: Bool
    ) throws -> ImageResizingResult {

        let image = try getImage(imagePath: imagePath)
        
        let imageDimensions = ImageDimensions(width: Int(image.size.width), height: Int(image.size.height))
        if (maxWidth == 0 || maxWidth >= imageDimensions.width) &&
           (maxHeight == 0 || maxHeight >= imageDimensions.height) {


            let fileUrl = URL(fileURLWithPath: imagePath)
            /*
            guard let webPath = bridge.portablePath(fromLocalURL: fileUrl) else {
                // throw .reject("Unable to get portable path to file")
                print("Unable to get portable path to file: \(imagePath)")
                throw ImageManipulatorError.failedToGetPortablePathToFile
                // return
            }
            */
            let webPath: String? = bridge.portablePath(fromLocalURL: fileUrl)?.absoluteString
            
            return ImageResizingResult(
                originalWidth: imageDimensions.width,
                originalHeight: imageDimensions.height,
                resizedWidth: imageDimensions.width,
                resizedHeight: imageDimensions.height,
                imagePath: imagePath,
                webPath: webPath,
                resized: false
            )
        }
        
        // Get the width and height of the image
        // let width = Int(image.size.width)
        // let height = Int(image.size.height)
        
        /*
        let imageResizingResult: ImageResizingResult = ImageResizingResult(
            originalWidth: 100, originalHeight: 200, resizedWidth: 300, resizedHeight: 400,
            imagePath: "imagePath", webPath: "webPath", resized: true
        )
        
        return imageResizingResult
        */
        
        // Resize and fix rotation (if fixRotation is true)
        let resizedImage: UIImage = image.resize(maxWidth: maxWidth, maxHeight: maxHeight, fixRotation: fixRotation)
        guard let resizedAndCompressedImageData: Data = resizedImage.jpegData(compressionQuality: CGFloat(Float(quality)) / 100.0) else {
            // let result = CDVPluginResult(status: .error, messageAs: "Failed to create image data")
            // self.commandDelegate.send(result, callbackId: command.callbackId)
            print("Failed to create image data")
            throw ImageManipulatorError.failedToCreateImageData
        }
        
        /*
        let cacheDirectory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
        let resizedImageURL = cacheDirectory.appendingPathComponent("img\(Date().timeIntervalSince1970).jpeg")
        */
        
        let resizedImageURL: URL? = try? saveResizedImage(data: resizedAndCompressedImageData, fileName: fileName)
        
        /*
        if (try? resizedAndCompressedImageData.write(to: resizedImagePath)) == nil {
            print("Failed to save resized image")
            throw ImageManipulatorError.failedToSaveResizedImage
        }
        */
        guard let resizedImageURL = resizedImageURL else {
            print("Failed to save resized image")
            throw ImageManipulatorError.failedToSaveResizedImage
        }
        
        if let resizedAndCompressedImage = UIImage(data: resizedAndCompressedImageData) {
            let resizedWidth = resizedAndCompressedImage.size.width
            let resizedHeight = resizedAndCompressedImage.size.height
            print("resizedWidth: \(resizedWidth), resizedHeight: \(resizedHeight)")
            
            let resizedWebPath: String? = bridge.portablePath(fromLocalURL: resizedImageURL)?.absoluteString
            
            return ImageResizingResult(
                originalWidth: imageDimensions.width,
                originalHeight: imageDimensions.height,
                resizedWidth: Int(resizedWidth),
                resizedHeight: Int(resizedHeight),
                imagePath: imagePath,
                webPath: resizedWebPath,
                resized: true
            )
    
        } else {
            print("Failed to get resized JPEG image from data")
            throw ImageManipulatorError.failedToGetResizedJPEGImageFromData
        }

    }
    
    func saveResizedImage(data: Data, fileName: String?) throws -> URL {
        // let fileName: String = fileName ?? "\(Date().timeIntervalSince1970)"
        
        let cacheDirectory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
        
        var url: URL
        
        /*
        if (fileName == nil){
            url = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("\(Date().timeIntervalSince1970).jpg")
        } else {
            var imageCounter: Int = 0
            repeat {
                if (imageCounter == 0){
                    url = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("\(fileName).jpg")
                } else {
                    imageCounter += 1
                    url = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("\(fileName)-\(imageCounter).jpg")
                }

            } while FileManager.default.fileExists(atPath: url.path)
        }
        */
        
        if let fileName = fileName {
            var imageCounter: Int = 0
            repeat {
                if (imageCounter == 0) {
                    url = cacheDirectory.appendingPathComponent("\(fileName).jpg")
                } else {
                    url = cacheDirectory.appendingPathComponent("\(fileName)-\(imageCounter).jpg")
                }
                imageCounter += 1
            } while FileManager.default.fileExists(atPath: url.path)
        } else {
            url = cacheDirectory.appendingPathComponent("\(Date().timeIntervalSince1970).jpg")
        }

        try data.write(to: url, options: .atomic)
        return url
    }
    
}
