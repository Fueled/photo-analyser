import SwiftUI

struct OnMoveDeleteListPlayground: View {
	@State var items0 = {
		var items: [Int] = []
		for i in 0..<10 {
			items.append(i)
		}
		return items
	}()
	@State var items1 = {
		var items: [Int] = []
		for i in 11..<20 {
			items.append(i)
		}
		return items
	}()
	@State var items2 = {
		var items: [Int] = []
		for i in 21..<30 {
			items.append(i)
		}
		return items
	}()

	@State var actionString = ""
	@State var action: () -> Void = {}
	@State var actionIsPresented = false

	var body: some View {
		List {
			Section(".onMove") {
				ForEach(items0, id: \.self) { item in
					Text("Item \(item)")
				}
				.onMove { fromOffsets, toOffset in
					actionString = "Move \(fromOffsets.count) item(s)"
					action = {
						withAnimation { items0.move(fromOffsets: fromOffsets, toOffset: toOffset) }
						action = {}
					}
					actionIsPresented = true
				}
			}
			Section(".onDelete") {
				ForEach(items1, id: \.self) { item in
					Text("Item \(item)")
				}
				.onDelete { offsets in
					actionString = "Delete \(offsets.count) item(s)"
					action = {
						withAnimation { items1.remove(atOffsets: offsets) }
						action = {}
					}
					actionIsPresented = true
				}
			}
			Section(".onMove, .onDelete") {
				ForEach(items2, id: \.self) { item in
					Text("Item \(item)")
				}
				.onMove { fromOffsets, toOffset in
					actionString = "Move \(fromOffsets.count) item(s)"
					action = {
						withAnimation { items2.move(fromOffsets: fromOffsets, toOffset: toOffset) }
						action = {}
					}
					actionIsPresented = true
				}
				.onDelete { offsets in
					actionString = "Delete \(offsets.count) item(s)"
					action = {
						withAnimation { items2.remove(atOffsets: offsets) }
						action = {}
					}
					actionIsPresented = true
				}
			}
		}
		.confirmationDialog(actionString, isPresented: $actionIsPresented) {
			Button(actionString, role: .destructive, action: action)
		}
	}
}

#Preview {
	OnMoveDeleteListPlayground()
}
