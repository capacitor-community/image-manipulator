import Foundation

@objc public class ImageManipulator: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
