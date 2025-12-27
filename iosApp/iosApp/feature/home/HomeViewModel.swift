import Foundation
import Shared
import Observation

@Observable
class HomeViewModel {
    // MARK: - State
    var wallpapers: [WallpaperUi] = []
    var searchWallpapers: [WallpaperUi] = []
    var isLoading: Bool = false
    var isPaginationLoading: Bool = false
    var errorMessage: String? = nil
    
    // Search State
    var isSearchMode: Bool = false
    var searchQuery: String = ""
    
    // Pagination State
    private var currentPage: Int = 1
    private var isEndReached: Bool = false
    
    // Dependencies
    private let repository: WallpaperRepository
    
    init() {
        self.repository = KoinHelper().wallpaperRepository
        loadCuratedWallpapers(reset: true)
    }
    
    // MARK: - Intents
    func loadCuratedWallpapers(reset: Bool = false) {
        if reset {
            self.isLoading = true
            self.currentPage = 1
            self.wallpapers = []
            self.isEndReached = false
            self.isSearchMode = false
        } else {
            guard !isPaginationLoading && !isEndReached else { return }
            self.isPaginationLoading = true
        }
        
        performFetch(query: nil, page: currentPage, isSearch: false)
    }
    
    func onSearchTriggered() {
        guard !searchQuery.isEmpty else { return }
        self.isSearchMode = true
        self.isLoading = true
        self.currentPage = 1
        self.searchWallpapers = []
        self.isEndReached = false
        performFetch(query: searchQuery, page: 1, isSearch: true)
    }
    
    func onClearSearch() {
        self.isSearchMode = false
        self.searchQuery = ""
        self.isEndReached = false
    }
    
    func loadNextPage() {
        guard !isPaginationLoading && !isEndReached else { return }
        self.isPaginationLoading = true
        let nextPage = currentPage + 1
        if isSearchMode {
            performFetch(query: searchQuery, page: nextPage, isSearch: true)
        } else {
            performFetch(query: nil, page: nextPage, isSearch: false)
        }
    }

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let kmWallpaper = wallpaper.toDomain()
                try await repository.toggleFavorite(wallpaper: kmWallpaper)

                updateWallpaperFavoriteStatus(wallpaperId: wallpaper.id)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
    
    // MARK: - Private Logic
    private func performFetch(query: String?, page: Int, isSearch: Bool) {
        Task {
            do {
                let result: [Wallpaper]
                if let query = query, isSearch {
                    result = try await repository.searchWallpapers(
                        query: query,
                        page: Int32(page)
                    )
                } else {
                    result = try await repository.getCuratedWallpapers(
                        page: Int32(page)
                    )
                }
                
                if result.isEmpty {
                    self.isEndReached = true
                    self.isLoading = false
                    self.isPaginationLoading = false
                } else {
                    let uiResults = result.map {
                        $0.toUi(isFavorite: $0.isFavorite)
                    }
                    
                    if isSearch {
                        if page == 1 {
                            self.searchWallpapers = uiResults
                        } else {
                            let existingIds = Set(self.searchWallpapers.map {
                                $0.id
                            })
                            let newUnique = uiResults.filter {
                                !existingIds.contains($0.id)
                            }
                            self.searchWallpapers.append(contentsOf: newUnique)
                        }
                    } else {
                        if page == 1 {
                            self.wallpapers = uiResults
                        } else {
                            let existingIds = Set(self.wallpapers.map { $0.id })
                            let newUnique = uiResults.filter {
                                !existingIds.contains($0.id)
                            }
                            self.wallpapers.append(contentsOf: newUnique)
                        }
                    }
                    self.currentPage = page
                    self.isLoading = false
                    self.isPaginationLoading = false
                }
            } catch {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
                self.isPaginationLoading = false
            }
        }
    }

    private func updateWallpaperFavoriteStatus(wallpaperId: Int64) {
        if let index = wallpapers.firstIndex(where: { $0.id == wallpaperId }) {
            wallpapers[index].isFavorite.toggle()
        }

        if let index = searchWallpapers.firstIndex(where: { $0.id == wallpaperId }) {
            searchWallpapers[index].isFavorite.toggle()
        }
    }
}
