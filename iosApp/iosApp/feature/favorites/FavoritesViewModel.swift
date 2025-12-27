import Foundation
import Shared
import Observation

@Observable
class FavoritesViewModel {
    // MARK: - State
    var favorites: [WallpaperUi] = []
    var isLoading: Bool = true
    var errorMessage: String? = nil
    var showToast: Bool = false
    var toastMessage: String = ""

    // Dependencies
    private let repository: WallpaperRepository
    
    init() {
        self.repository = KoinHelper().wallpaperRepository
        observeFavorites()
    }

    // MARK: - Intents
    func removeFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                try await repository.removeFavorite(wallpaperId: wallpaper.id)
                self.toastMessage = "Removed from favorites"
                self.showToast = true
            } catch {
                self.errorMessage = error.localizedDescription
                self.toastMessage = "Failed to remove favorite"
                self.showToast = true
            }
        }
    }

    func clearAllFavorites() {
        Task {
            do {
                for favorite in favorites {
                    try await repository.removeFavorite(wallpaperId: favorite.id)
                }
                self.toastMessage = "All favorites cleared"
                self.showToast = true
            } catch {
                self.errorMessage = error.localizedDescription
                self.toastMessage = "Failed to clear favorites"
                self.showToast = true
            }
        }
    }

    // MARK: - Private Logic
    private func observeFavorites() {
        repository.observeFavorites().collect(
            collector: Collector<[Wallpaper]> { [weak self] wallpapers in
                guard let self = self else {
                    return
                }
                Task { @MainActor in
                    self.favorites = wallpapers.map {
                        $0.toUi(isFavorite: true)
                    }
                    self.isLoading = false
                }
            },
            completionHandler: { [weak self] error in
                guard let self = self else {
                    return
                }
                Task { @MainActor in
                    if let error = error {
                        self.errorMessage = error.localizedDescription
                    }
                    self.isLoading = false
                }
            }
        )
    }
}
