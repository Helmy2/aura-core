//
//  ImageDownloader.swift
//  iosApp
//
//  Created by platinum on 25/12/2025.
//

import Foundation
import UIKit
import Photos

class ImageDownloader {
    
    /// Downloads image from URL and saves to Photo Library.
    /// Returns true if successful, false otherwise.
    func downloadAndSave(url: String) async -> Bool {
        guard let imageUrl = URL(string: url),
              let data = try? Data(contentsOf: imageUrl),
              let image = UIImage(data: data) else {
            print("Failed to download image data")
            return false
        }
        
        return await saveToPhotoLibrary(image)
    }
    
    private func saveToPhotoLibrary(_ image: UIImage) async -> Bool {
        return await withCheckedContinuation { continuation in
            PHPhotoLibrary.requestAuthorization { status in
                guard status == .authorized || status == .limited else {
                    print("Permission denied")
                    continuation.resume(returning: false)
                    return
                }
                
                PHPhotoLibrary.shared().performChanges {
                    PHAssetChangeRequest.creationRequestForAsset(from: image)
                } completionHandler: { success, error in
                    if let error = error {
                        print("Error saving photo: \(error)")
                    }
                    continuation.resume(returning: success)
                }
            }
        }
    }
}
