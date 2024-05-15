//
//  HomeViewModel.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

final class HomeViewModel: ObservableObject {
	@Published var isPickerActionSheetPresented = false

	func plusButtonTapped() {
		isPickerActionSheetPresented = true
	}

	func chooseFromLibraryActionTapped() {

	}

	func openCameraTapped() {

	}
}
