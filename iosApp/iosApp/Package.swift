// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "iosApp",
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
        //.package(url: "https://github.com/vyshane/grpc-swift-combine.git", .upToNextMajor(from: "1.0.13"))
        .package(url: "https://github.com/grpc/grpc-swift.git", from: "1.0.0-alpha.8")
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .executableTarget(
            name: "iosApp",
            dependencies: []),
            //dependencies: [.product(name: "GRPC", package: "grpc-swift")]),
        .testTarget(
            name: "iosAppTests",
            dependencies: ["iosApp"]),
    ]
)
