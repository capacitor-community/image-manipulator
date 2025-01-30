import Capacitor
import Foundation

@objc public class ImageManipulator: NSObject {
    
    private var bridge: CAPBridgeProtocol

    init(bridge: CAPBridgeProtocol) {
        self.bridge = bridge
    }
    
    func getImage(imagePath: String) throws -> UIImage {
        
        var sourceImage: UIImage?
        
        // NOTE: Load image
        if FileManager.default.fileExists(atPath: imagePath) {
            sourceImage = UIImage(contentsOfFile: imagePath)
        } else if let url = URL(string: imagePath), let data = try? Data(contentsOf: url) {
            sourceImage = UIImage(data: data)
        }

        guard let sourceImage = sourceImage else {
            throw ImageManipulatorError.failedToLoadImage
        }
        
        return sourceImage
    }
    
    func getDimensions(imagePath: String) throws -> ImageDimensions {

        let image = try getImage(imagePath: imagePath)
        let imageDimensions = ImageDimensions(width: Int(image.size.width), height: Int(image.size.height))
        return imageDimensions
    }
    
    func resize(
        imagePath: String, fileName: String?, quality: Int,
        maxWidth: Int, maxHeight: Int, fixRotation: Bool
    ) throws -> ImageResizingResult {

        var image = try getImage(imagePath: imagePath)
        
        let imageDimensions = ImageDimensions(width: Int(image.size.width), height: Int(image.size.height))
        if (maxWidth == 0 || maxWidth >= imageDimensions.width) &&
           (maxHeight == 0 || maxHeight >= imageDimensions.height) {

            let fileUrl = URL(fileURLWithPath: imagePath)
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
        
        if (fixRotation) {
            // NOTE: apply the EXIF orientation before resizing the image, ensuring it displays correctly
            image = image.fixOrientation()
        }
        
        // NOTE: resize the image to the required dimensions
        let resizedImage: UIImage = image.resize(maxWidth: maxWidth, maxHeight: maxHeight)
        
        print("resizedImage size: \(resizedImage.size)")
        print("resizedImage scale: \(resizedImage.scale)")
        
        let jpegQuality = min(abs(CGFloat(quality)) / 100.0, 1.0)
        guard let resizedAndCompressedImageData: Data = resizedImage.jpegData(compressionQuality: jpegQuality) else {
            throw ImageManipulatorError.failedToCreateImageData
        }
        
        let finalImage: UIImage = UIImage(data: resizedAndCompressedImageData)!
        print("finalImage size: \(finalImage.size)")
        print("finalImage scale: \(finalImage.scale)")

        let resizedImageURL: URL? = try? saveResizedImage(data: resizedAndCompressedImageData, fileName: fileName)
        
        guard let resizedImageURL = resizedImageURL else {
            throw ImageManipulatorError.failedToSaveResizedImage
        }
        
        if let resizedAndCompressedImage = UIImage(data: resizedAndCompressedImageData) {
            let resizedWidth = resizedAndCompressedImage.size.width
            let resizedHeight = resizedAndCompressedImage.size.height
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
            throw ImageManipulatorError.failedToGetResizedJPEGImageFromData
        }

    }
    
    func saveResizedImage(data: Data, fileName: String?) throws -> URL {
        
        let cacheDirectory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
        
        var url: URL
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
