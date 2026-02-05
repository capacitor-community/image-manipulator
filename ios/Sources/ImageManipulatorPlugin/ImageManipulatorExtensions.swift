//
//  ImageManipulatorExtensions.swift
//  Pods
//
//  Created by Alex Ryltsov on 1/29/25.
//

import UIKit

extension UIImage {

    /// Method to get the final image dimensions
    ///
    /// - Parameter maxWidth required image width
    /// - Parameter maxHeight required image width
    /// - Returns: Final image dimensions
    func getResultingImageDimensions(maxWidth: Int, maxHeight: Int) -> CGSize {
        let originalWidth = Int(self.size.width)
        let originalHeight = Int(self.size.height)

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        // NOTE: if both maxWidth and maxHeight are not provided, return original dimensions
        if finalWidth <= 0 && finalHeight <= 0 {
            finalWidth = originalWidth
            finalHeight = originalHeight
        }
        // NOTE: when only maxWidth is provided
        else if finalWidth > 0 && finalHeight <= 0 {
            finalHeight = Int(
                (Float(finalWidth) / Float(originalWidth))
                    * Float(originalHeight))
        }
        // NOTE: when only maxHeight is provided
        else if finalWidth <= 0 {
            finalWidth = Int(
                (Float(finalHeight) / Float(originalHeight))
                    * Float(originalWidth))
        }
        // NOTE: when both maxWidth and maxHeight are provided, maintain aspect ratio
        else {
            let originalAspectRatio =
                Float(originalWidth) / Float(originalHeight)
            if Float(maxWidth) / Float(maxHeight) > originalAspectRatio {
                finalWidth = Int(Float(maxHeight) * originalAspectRatio)
            } else {
                finalHeight = Int(Float(maxWidth) / originalAspectRatio)
            }
        }

        let scale: Float = Float(self.scale)
        return CGSize(width: Int(Float(finalWidth) / scale), height: Int(Float(finalHeight) / scale))
    }

    /// Method to resize image
    ///
    /// - Parameter maxWidth required image width
    /// - Parameter maxHeight required image width
    /// - Returns: Resized image
    func resize(maxWidth: Int, maxHeight: Int) -> UIImage {
        guard let cgImage = self.cgImage else { return self }

        let finalSize: CGSize = self.getResultingImageDimensions(maxWidth: maxWidth, maxHeight: maxHeight)

        // NOTE: normalize scale to 1.0 to avoid unexpected size increases
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1.0
        
        // NOTE: draw the resized image
        let renderer = UIGraphicsImageRenderer(size: finalSize, format: format)
        let resizedImage = renderer.image { _ in
            self.draw(in: CGRect(origin: .zero, size: finalSize))
        }
        return resizedImage
    }
    
    /// Method to correct or fix image orientation based on EXIF orientation, and ensure the image to be oriented properly.
    ///
    /// - Returns: correctly oriented image
    func fixOrientation() -> UIImage {
        // NOTE: ensure that the image has a valid cgImage (underlying pixel data)
        guard self.cgImage != nil else { return self }

        // NOTE: create a new image context with the current image's size and scale
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1.0  // Prevent scaling by the screen's scale factor

        // NOTE: create a renderer to apply the fix (draw the image correctly based on orientation)
        let renderer = UIGraphicsImageRenderer(size: size, format: format)

        // NOTE: Draw the image into the renderer context
        return renderer.image { _ in
            self.draw(in: CGRect(origin: .zero, size: size))
        }
    }

}
