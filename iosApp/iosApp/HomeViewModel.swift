import Shared
import SwiftUI

@MainActor
@Observable class HomeViewModel {

    var wallpapers: [Wallpaper] = []
    var isLoading: Bool = false
    var errorMessage: String? = nil

    private let repository: WallpaperRepository

    init() {
        self.repository = KoinHelper().wallpaperRepository
        loadCuratedWallpapers()
    }

    func loadCuratedWallpapers() {
        self.isLoading = true
        self.errorMessage = nil
        
        Task {
            do {
                let result = try await repository.getCuratedWallpapers()

                self.wallpapers = result
                self.isLoading = false
            } catch {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
            }
        }
    }
}
