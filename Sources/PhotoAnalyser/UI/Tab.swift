//
//  Tab.swift
//
//
//  Created by Hadi Dbouk on 13/05/2024.
//

import SwiftUI

enum Tab {
	case home
	case settings
}

extension Tab {
	var title: String {
		switch self {
		case .home:
			String(localized: "Home")
		case .settings:
			String(localized: "Settings")
		}
	}

	var iconName: String {
		switch self {
		case .home:
			"house.fill"
		case .settings:
			"gearshape.fill"
		}
	}
}
