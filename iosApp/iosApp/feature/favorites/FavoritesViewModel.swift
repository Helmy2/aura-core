import Foundation
import Observation
import Shared

@Observable
class FavoritesViewModel {

    // MARK: - State
    var favorites: [WallpaperUi] = []
    var isLoading: Bool = true
    var errorMessage: String? = nil

    private let repository: WallpaperRepository
    private let favoritesRepository: FavoritesRepository

    private var observationTask: Task<Void, Never>? = nil

    init() {
        self.repository = iOSApp.dependencies.wallpaperRepository
        self.favoritesRepository = iOSApp.dependencies.favoritesRepository
    }

    // MARK: - Lifecycle

    func startObserving() {
        stopObserving()

        isLoading = true

        observationTask = Task { @MainActor in
            for await wallpapers in favoritesRepository.observeFavoritesWallpapers() {
                let uiList = wallpapers.map {
                    $0.toUi()
                }

                self.favorites = uiList
                self.isLoading = false
            }
        }
    }

    func stopObserving() {
        observationTask?.cancel()
        observationTask = nil
    }

    // MARK: - Intents

    func removeFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                try await favoritesRepository.toggleFavorite(wallpaper: wallpaper.toDomain())
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}
