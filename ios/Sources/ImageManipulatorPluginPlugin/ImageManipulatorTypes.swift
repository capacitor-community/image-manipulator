/*
 struct CameraPromptText {
    let title: String
    let photoAction: String
    let cameraAction: String
    let cancelAction: String

    init(title: String? = nil, photoAction: String? = nil, cameraAction: String? = nil, cancelAction: String? = nil) {
        self.title = title ?? "Photo"
        self.photoAction = photoAction ?? "From Photos"
        self.cameraAction = cameraAction ?? "Take Picture"
        self.cancelAction = cancelAction ?? "Cancel"
    }
}

public struct CameraResult {
    let image: UIImage?
    let metadata: [AnyHashable: Any]
}
*/

public enum ImageManipulatorError: Error {
    // case invalidURL
    case failedToLoadImage
    // case failedToGetPortablePathToFile
    case failedToCreateImageData
    case failedToSaveResizedImage
    case failedToGetResizedJPEGImageFromData
}

struct ImageDimensions {
    let width: Int
    let height: Int

    init(width: Int, height: Int) {
        self.width = width
        self.height = height
    }
}

struct ImageResizingResult {
    let originalWidth: Int
    let originalHeight: Int
    let resizedWidth: Int
    let resizedHeight: Int
    let imagePath: String
    let webPath: String?
    let resized: Bool

    init(
        originalWidth: Int, originalHeight: Int, resizedWidth: Int,
        resizedHeight: Int, imagePath: String, webPath: String?, resized: Bool
    ) {
        self.originalWidth = originalWidth
        self.originalHeight = originalHeight
        self.resizedWidth = resizedWidth
        self.resizedHeight = resizedHeight
        self.imagePath = imagePath
        self.webPath = webPath
        self.resized = resized
    }
}
