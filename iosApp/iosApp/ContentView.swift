import SwiftUI
import shared
import GRPC
import NIO
import NIOSSL



struct ContentView: View {
    @State private var request: String = ""
    @State private var response: String = ""
    var body: some View {
        VStack {
            TextField("Input Here: ", text: $request)
            .textFieldStyle(.roundedBorder)
            .cornerRadius(4.0)
            .padding()
                                  
        }.padding()
        Button(action: submit) {
            HStack(alignment: .center) {
                Spacer()
                Text("Response").font(.subheadline)
                Spacer()
            }
                            
        }.frame(width: 100, height: 10)
            .padding()
            .background(Color.green)
            .cornerRadius(4.0)
        
        Text(self.response)
            .font(.subheadline)
            .padding(EdgeInsets(top: 70, leading: 0, bottom: 70, trailing: 0))
      }
    
    var connection: ClientConnection?
    init() {
      let conf = ClientConnection.Configuration.default(target: .hostAndPort("localhost", 8080), eventLoopGroup: MultiThreadedEventLoopGroup(numberOfThreads: 1))
      connection = ClientConnection.init(configuration: conf)
        print(conf)
    }
    
    func submit (){
        sayHello(request)
    }
    
    func sayHello(_ request: String) {
        guard let connection = connection else {
            print("**************************")
            return
        }
        var helloRequest = HelloRequest.init()
        helloRequest.name = request
        let client = GreeterNIOClient(channel: connection)
        _ = client.sayHello(helloRequest).response.always { result in
            switch result {
            case let .success(helloReply):
                self.response = helloReply.message
            case let .failure(error):
                print(error)
                self.response = "Oop! Something wrong :("
            }
        }
        
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
