// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "Shared",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "Shared",
            targets: ["Shared"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "Shared",
            url: "https://github.com/Helmy2/aura-core/releases/download/v0.0.3/Shared.xcframework.zip",
            checksum: "0465f8b7b96cac121d05619b86ef76c60bbaa6ec74d2278825414f98793ff23c"
        ),
    ]
)