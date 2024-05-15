//
//  HomeViewModel.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

final class HomeViewModel: ObservableObject {
	@Published var isPickerActionSheetPresented = false
	@Published var photos = [
		"https://images.pexels.com/photos/417074/pexels-photo-417074.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/381739/pexels-photo-381739.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/709552/pexels-photo-709552.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/600114/pexels-photo-600114.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/2240000/pexels-photo-2240000.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/45853/grey-crowned-crane-bird-crane-animal-45853.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/884788/pexels-photo-884788.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/459203/pexels-photo-459203.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/4064432/pexels-photo-4064432.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/2444429/pexels-photo-2444429.jpeg?auto=compress&cs=tinysrgb&w=1200",
		"https://images.pexels.com/photos/460775/pexels-photo-460775.jpeg?auto=compress&cs=tinysrgb&w=1200",
	]

	func plusButtonTapped() {
		isPickerActionSheetPresented = true
	}

	func chooseFromLibraryActionTapped() {

	}

	func openCameraTapped() {

	}
}
