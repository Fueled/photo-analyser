//
//  HomeView.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

struct HomeView: View {
	@ObservedObject var viewModel: HomeViewModel

    var body: some View {
		NavigationStack {
			List {
				ForEach(viewModel.photos, id: \.self) { urlString in
					AsyncImage(url: URL(string: urlString)! ){ result in
						result.image?
							.resizable()
							.scaledToFill()
					}
					.frame(maxWidth: .infinity, minHeight: 200, maxHeight: 200)
					#if !SKIP
					.listRowInsets(EdgeInsets())
					#endif
				}
				.onDelete { indexSet in
					viewModel.onDelete(indexSet)
				}
			}
			.confirmationDialog("", isPresented: $viewModel.isPickerActionSheetPresented) {
				Button {
					viewModel.openCameraActionButtonTapped()
				} label: {
					Text("Open Camera")
				}
				Button("Choose Photo Library") {
					viewModel.chooseFromLibraryActionButtonTapped()
				}
			} message: {
				Text("Import your image")
			}
			.toolbar {
				ToolbarItem(placement: .principal) {
					Text("Home")
						.fontWeight(.bold)
				}

				ToolbarItem(placement: .topBarTrailing) {
					Button {
						viewModel.plusButtonTapped()
					} label: {
						Image(systemName: "plus")
							.resizable()
							.frame(width: 16, height: 16)
					}
				}
			}
		}
    }
}

#Preview {
	HomeView(viewModel: .init())
}
