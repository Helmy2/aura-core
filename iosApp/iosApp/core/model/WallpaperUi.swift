//
//  WallpaperUi.swift
//  iosApp
//
//  Created by platinum on 26/12/2025.
//

import Foundation
import SwiftUI
import Shared

struct WallpaperUi: Identifiable, Equatable {
    let id: Int64
    let imageUrl: String
    let smallImageUrl: String
    let photographerName: String
    let contentDescription: String
    let averageColor:String
    let aspectRatio: CGFloat
}

extension Wallpaper {
    func toUi() -> WallpaperUi {
        let h = Double(self.height)
        let w = Double(self.width)
        let ratio = h > 0 ? (w / h) : 0.7

        return WallpaperUi(
            id: self.id,
            imageUrl: self.imageUrl,
            smallImageUrl: self.smallImageUrl,
            photographerName: "Photo by \(self.photographer)",
            contentDescription: "Photo by \(self.photographer)",
            averageColor: self.averageColor,
            aspectRatio: CGFloat(ratio)
        )
    }
}
