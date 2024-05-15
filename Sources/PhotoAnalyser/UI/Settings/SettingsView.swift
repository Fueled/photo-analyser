//
//  SettingsView.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

struct SettingsView: View {
	@ObservedObject var viewModel: SettingsViewModel

    var body: some View {
        Text("Settings")
    }
}

#Preview {
	SettingsView(viewModel: .init())
}
