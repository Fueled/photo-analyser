//
//  AppViewModel.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

final class AppViewModel: ObservableObject {
	@Published var currentTab = Tab.home

	let homeViewModel = HomeViewModel()
	let settingsViewModel = SettingsViewModel()
}
