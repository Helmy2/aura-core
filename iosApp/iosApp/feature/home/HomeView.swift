import SwiftUI
import Shared

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    
    // Moved columns definition here
    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        NavigationStack {
            content
                .navigationBarHidden(true)
        }
    }
    
    // MARK: - Subviews
    
    private var content: some View {
        VStack(spacing: 0) {
            searchBarSection
            
            // Check loading state for Search Mode specifically
            if viewModel.isLoading && viewModel.isSearchMode && viewModel.searchWallpapers.isEmpty {
                loadingSpinner
            } else {
                wallpaperGridScrollView
            }
        }
    }
    
    private var searchBarSection: some View {
        SearchBarView(
            text: $viewModel.searchQuery,
            isSearchActive: viewModel.isSearchMode,
            onSearch: { viewModel.onSearchTriggered() },
            onClear: { viewModel.onClearSearch() }
        )
        .padding(.horizontal)
        .padding(.top, 8)
        .padding(.bottom, 8)
    }
    
    private var loadingSpinner: some View {
        ProgressView()
            .scaleEffect(1.5)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    private var wallpaperGridScrollView: some View {
        ScrollView {
            wallpaperGrid
                .padding(.horizontal)
                .padding(.top, 8)
        }
    }
    
    private var wallpaperGrid: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            // Determine which list to use
            let list = viewModel.isSearchMode ? viewModel.searchWallpapers : viewModel.wallpapers
            
            ForEach(list, id: \.id) { wallpaper in
                NavigationLink(destination: DetailView(wallpaper: wallpaper)) {
                    WallpaperItemView(wallpaper: wallpaper)
                        .onAppear {
                            if wallpaper == list.last {
                                viewModel.loadNextPage()
                            }
                        }
                }
                .buttonStyle(PlainButtonStyle())
            }
            
            if viewModel.isPaginationLoading {
                paginationLoader
            }
        }
    }
    
    private var paginationLoader: some View {
        ProgressView()
            .frame(maxWidth: .infinity)
            .padding()
    }
}

// MARK: - Helper Components

struct SearchBarView: View {
    @Binding var text: String
    var isSearchActive: Bool
    var onSearch: () -> Void
    var onClear: () -> Void
    
    var body: some View {
        HStack {
            if isSearchActive {
                Button(action: onClear) {
                    Image(systemName: "arrow.left")
                        .font(.title2)
                        .foregroundColor(.primary)
                }
                .transition(.move(edge: .leading).combined(with: .opacity))
            }
            
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                
                TextField("Search wallpapers...", text: $text)
                    .submitLabel(.search)
                    .onSubmit {
                        onSearch()
                    }
                
                if !text.isEmpty {
                    Button(action: { text = "" }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                }
            }
            .padding(10)
            .background(Color(.systemGray6))
            .cornerRadius(10)
        }
        .animation(.default, value: isSearchActive)
    }
}

struct WallpaperItemView: View {
    let wallpaper: WallpaperUi
    
    var body: some View {
        VStack(alignment: .leading) {
            AsyncImage(url: URL(string: wallpaper.smallImageUrl)) { phase in
                switch phase {
                case .empty:
                    Color.gray.opacity(0.2)
                case .success(let image):
                    image.resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(minWidth: 0, maxWidth: .infinity)
                case .failure:
                    Color.red.opacity(0.2)
                @unknown default:
                    EmptyView()
                }
            }
            .frame(height: 220)
            .cornerRadius(12)
            .clipped()
        }
    }
}
