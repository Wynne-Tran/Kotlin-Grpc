////
////  GreeterRCP.swift
////  iosApp
////
////  Created by graphic on 2022-10-07.
////  Copyright © 2022 orgName. All rights reserved.
////
//
//import Foundation
//import GRPC
//import NIO
//import NIOSSL
//
//class GreeterRCP {
//    var connection: ClientConnection?
//    init() {
//      let conf = ClientConnection.Configuration.default(target: .hostAndPort("10.0.2.2", 8080), eventLoopGroup: MultiThreadedEventLoopGroup(numberOfThreads: 1))
//      connection = ClientConnection.init(configuration: conf)
//      sayHello()
//    }
//    
//    func sayHello() {
//      guard let connection = connection else { return }
//      var helloRequest = HelloRequest.init()
//      helloRequest.name = "SwiftUI"
//      let client = GreeterClient(channel: connection)
//      _ = client.sayHello(helloRequest).response.always { result in
//        switch result {
//        case let .success(helloResponse):
//          print(helloResponse.message)
//        case let .failure(error):
//          print(error)
//        }
//      }
//    }
//    
//}
//
