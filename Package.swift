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
            url: "URL_PLACEHOLDER",
            checksum: "CHECKSUM_PLACEHOLDER"
        ),
    ]
)