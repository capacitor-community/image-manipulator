// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorCommunityImageManipulator",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorCommunityImageManipulator",
            targets: ["ImageManipulatorPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ImageManipulatorPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ImageManipulatorPlugin"),
        .testTarget(
            name: "ImageManipulatorPluginTests",
            dependencies: ["ImageManipulatorPlugin"],
            path: "ios/Tests/ImageManipulatorPluginTests")
    ]
)
