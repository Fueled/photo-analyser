import XCTest
import OSLog
import Foundation
@testable import PhotoAnalyser

let logger: Logger = Logger(subsystem: "PhotoAnalyser", category: "Tests")

@available(macOS 13, *)
final class PhotoAnalyserTests: XCTestCase {
    func testPhotoAnalyser() throws {
        logger.log("running testPhotoAnalyser")
        XCTAssertEqual(1 + 2, 3, "basic test")
        
        // load the TestData.json file from the Resources folder and decode it into a struct
        let resourceURL: URL = try XCTUnwrap(Bundle.module.url(forResource: "TestData", withExtension: "json"))
        let testData = try JSONDecoder().decode(TestData.self, from: Data(contentsOf: resourceURL))
        XCTAssertEqual("PhotoAnalyser", testData.testModuleName)
    }
}

struct TestData : Codable, Hashable {
    var testModuleName: String
}