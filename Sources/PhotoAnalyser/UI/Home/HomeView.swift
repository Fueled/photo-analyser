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
				ForEach(0..<10, id: \.self) {
					Text("\($0)")
				}
			}
			#if !SKIP
			.actionSheet(isPresented: $viewModel.isPickerActionSheetPresented) {
				ActionSheet(
					title: Text("Import your image"),
					buttons: [
						.default(
							Text("Choose from library"),
							action: {
								viewModel.chooseFromLibraryActionTapped()
							}
						),
						.default(
							Text("Open Camera"),
							action: {
								viewModel.openCameraTapped()
							}
						),
						.cancel(),
					]
				)
			}
			#endif
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
			#if SKIP
			.overlay {
				if viewModel.isPickerActionSheetPresented {
//					ComposeView { _ in
//						androidx.compose.ModalBottomSheet(onDismissRequest = { /* Executed when the sheet is dismissed */ }) {
//							// Sheet content
//						}
//					}
				}
			}
			#endif
		}
    }
}

#Preview {
	HomeView(viewModel: .init())
}
