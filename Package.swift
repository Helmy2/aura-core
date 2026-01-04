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
            url: "https://github.com/Helmy2/aura-core/releases/download/v0.0.1/Shared.xcframework.zip",
            checksum: "4bef1a5a12ac1a87b0872a0c709b3dc5055a9535b8948e504c50988e25958835"
        ),
    ]
)
