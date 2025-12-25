//
//  DetailViewModel.swift
//  iosApp
//
//  Created by platinum on 25/12/2025.
//

import Foundation
import SwiftUI
import Observation

@MainActor
@Observable
class DetailViewModel {
    var isDownloading: Bool = false
    var showToast: Bool = false
    var toastMessage: String = ""
    
    private let downloader = ImageDownloader()
    
    func downloadWallpaper(url: String) {
        guard !isDownloading else { return }
        self.isDownloading = true
        
        Task {
            let success = await downloader.downloadAndSave(url: url)
            self.isDownloading = false
            self.toastMessage = success ? "Saved to Photos" : "Save Failed"
            self.showToast = true
        }
    }
}
