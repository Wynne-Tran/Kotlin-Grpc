package dandelion.net.server

import dandelion.net.protos.GreeterGrpcKt
import dandelion.net.protos.HelloReply
import dandelion.net.protos.HelloRequest
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors


class HelloServer(private val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(HelloService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@HelloServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }
    fun blockUntilShutdown() {
        server.awaitTermination()
    }


    private class HelloService : GreeterGrpcKt.GreeterCoroutineImplBase(
        coroutineContext = Executors.newFixedThreadPool(
            1
        ).asCoroutineDispatcher()) {
        override suspend fun sayHello(request: HelloRequest) : HelloReply {
            var response = 0
            var message = ""
            try {
                response = 2022 - request.name.toInt();
                message = response.toString()
            }
            catch (_: NumberFormatException){
                message = "Please input a number !"
            }
            return HelloReply.newBuilder()
                .setMessage("Your age is: $message")
                .build()
        }
    }

}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = HelloServer(port)
    server.start()
    server.blockUntilShutdown()
}