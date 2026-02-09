public enum ImageManipulatorError: Error {
    case failedToLoadImage
    case failedToCreateImageData
    case failedToSaveResizedImage
    case failedToGetResizedJPEGImageFromData
}

struct ImageDimensions {
    let width: Int
    let height: Int
}

struct ImageResizingResult {
    let originalWidth: Int
    let originalHeight: Int
    let resizedWidth: Int
    let resizedHeight: Int
    let imagePath: String
    let webPath: String?
    let resized: Bool
}
