// swift-tools-version:5.3
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://maven.pkg.github.com/Helmy2/aura-core/com/helmy/aura/shared-kmmbridge/0.0.1/shared-kmmbridge-0.0.1.zip"
let remoteKotlinChecksum = "e3d70ad118debe609444ac53d45dfd2194537b579ec30b270aa501d713fdb211"
let packageName = "Shared"
// END KMMBRIDGE BLOCK

let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName]
        ),
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        )
        ,
    ]
)