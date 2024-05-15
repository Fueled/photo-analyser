//
//  AppView.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

struct AppView: View {
	@StateObject private var viewModel = AppViewModel()

    var body: some View {
		TabView(selection: $viewModel.currentTab) {
			HomeView(viewModel: viewModel.homeViewModel)
				.tabItem { Label(Tab.home.title, systemImage: Tab.home.iconName) }
				.tag(Tab.home)
			SettingsView(viewModel: viewModel.settingsViewModel)
				.tabItem { Label(Tab.settings.title, systemImage: Tab.settings.iconName) }
				.tag(Tab.settings)
		}
    }
}

#Preview {
	AppView()
}
