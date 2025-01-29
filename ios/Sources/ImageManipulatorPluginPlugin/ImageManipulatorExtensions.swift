//
//  ImageManipulatorExtensions.swift
//  Pods
//
//  Created by Alex Ryltsov on 1/29/25.
//

import UIKit

extension UIImage {

    func getResultingImageDimensions(maxWidth: Int, maxHeight: Int) -> CGSize {
        let originalWidth = Int(self.size.width)
        let originalHeight = Int(self.size.height)

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        // If both maxWidth and maxHeight are not provided, return original dimensions
        if finalWidth <= 0 && finalHeight <= 0 {
            finalWidth = originalWidth
            finalHeight = originalHeight
        }
        // When only maxWidth is provided
        else if finalWidth > 0 && finalHeight <= 0 {
            finalHeight = Int(
                (Float(finalWidth) / Float(originalWidth))
                    * Float(originalHeight))
        }
        // When only maxHeight is provided
        else if finalWidth <= 0 {
            finalWidth = Int(
                (Float(finalHeight) / Float(originalHeight))
                    * Float(originalWidth))
        }
        // When both maxWidth and maxHeight are provided, maintain aspect ratio
        else {
            let originalAspectRatio =
                Float(originalWidth) / Float(originalHeight)
            if Float(maxWidth) / Float(maxHeight) > originalAspectRatio {
                finalWidth = Int(Float(maxHeight) * originalAspectRatio)
            } else {
                finalHeight = Int(Float(maxWidth) / originalAspectRatio)
            }
        }

        return CGSize(width: finalWidth, height: finalHeight)
    }

    func resize(
        // to targetSize: CGSize? = nil,
        maxWidth: Int, maxHeight: Int, fixRotation: Bool = true
    ) -> UIImage {
        guard let cgImage = self.cgImage else { return self }

        var transform = CGAffineTransform.identity
        var originalSize = CGSize(width: cgImage.width, height: cgImage.height)

        if fixRotation {
            let orientation = self.imageOrientation

            // Calculate the transformation based on the EXIF orientation
            switch orientation {
            case .down, .downMirrored:
                transform = transform.rotated(by: .pi)
            case .left, .leftMirrored:
                transform = transform.rotated(by: .pi / 2)
            case .right, .rightMirrored:
                transform = transform.rotated(by: -.pi / 2)
            default:
                break
            }

            // Apply mirroring for mirrored orientations
            switch orientation {
            case .upMirrored, .downMirrored:
                transform = transform.translatedBy(
                    x: CGFloat(cgImage.width), y: 0
                ).scaledBy(x: -1, y: 1)
            case .leftMirrored, .rightMirrored:
                transform = transform.translatedBy(
                    x: CGFloat(cgImage.height), y: 0
                ).scaledBy(x: -1, y: 1)
            default:
                break
            }

            // Swap width & height for 90° or 270° rotations
            if orientation == .left || orientation == .leftMirrored
                || orientation == .right || orientation == .rightMirrored
            {
                originalSize = CGSize(
                    width: originalSize.height, height: originalSize.width)
            }
        }

        // Determine new size while maintaining aspect ratio
        /*
        var newSize = originalSize
        if let targetSize = targetSize {
            let widthRatio = targetSize.width / originalSize.width
            let heightRatio = targetSize.height / originalSize.height
            let scaleFactor = min(widthRatio, heightRatio)  // Maintain aspect ratio
            newSize = CGSize(
                width: originalSize.width * scaleFactor,
                height: originalSize.height * scaleFactor)
        }
        */
        let newSize: CGSize = self.getResultingImageDimensions(maxWidth: maxWidth, maxHeight: maxHeight)

        // Draw the transformed and resized image
        let renderer = UIGraphicsImageRenderer(size: newSize)
        let newImage = renderer.image { context in
            let ctx = context.cgContext
            ctx.translateBy(x: newSize.width / 2, y: newSize.height / 2)
            if fixRotation {
                ctx.concatenate(transform)
            }
            ctx.translateBy(
                x: -originalSize.width / 2, y: -originalSize.height / 2)
            ctx.draw(
                cgImage,
                in: CGRect(
                    x: 0, y: 0, width: originalSize.width,
                    height: originalSize.height))
        }

        return newImage
    }

}
