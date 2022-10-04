package dandelion.net.server

import dandelion.net.protos.*
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors


class CalculateAgeServer(private val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(CalculateAgeService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@CalculateAgeServer.stop()
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


    private class CalculateAgeService : CalculateAgeGrpcKt.CalculateAgeCoroutineImplBase(
        coroutineContext = Executors.newFixedThreadPool(
            1
        ).asCoroutineDispatcher()
    ) {
        override suspend fun calculateAge(request: BirthYearRequest): AgeReply {
            val response = request.birthYear + 10
            return AgeReply.newBuilder().setMessage("After 10 years, your age will be: $response")
                .build()
        }
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8089
    val server = CalculateAgeServer(port)
    server.start()
    server.blockUntilShutdown()
}