import SwiftUI
import Shared



struct HomeView: View {
    @State private var viewModel = HomeViewModel()
    
    let columns = [
        GridItem(.adaptive(minimum: 150), spacing: 10)
    ]

    var body: some View {
        NavigationView {
            ZStack {
                if viewModel.isLoading {
                    ProgressView()
                } else if let error = viewModel.errorMessage {
                    Text("Error: \(error)")
                        .foregroundColor(.red)
                } else {
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 10) {
                            ForEach(viewModel.wallpapers, id: \.id) { wallpaper in
                                WallpaperItemView(wallpaper: wallpaper)
                            }
                        }
                        .padding()
                    }
                }
            }
        }
    }
}

struct WallpaperItemView: View {
    let wallpaper: Wallpaper
    
    var body: some View {
        AsyncImage(url: URL(string: wallpaper.smallImageUrl)) { image in
            image
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(minWidth: 0, maxWidth: .infinity)
                .frame(height: 200)
                .clipped()
        } placeholder: {
            Color.gray.opacity(0.3)
                .frame(height: 200)
        }
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
