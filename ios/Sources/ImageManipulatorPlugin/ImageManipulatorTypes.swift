public enum ImageManipulatorError: Error {
    case failedToLoadImage
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
