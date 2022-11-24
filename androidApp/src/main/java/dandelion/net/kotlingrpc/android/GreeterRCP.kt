package dandelion.net.kotlingrpc.android

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import dandelion.net.protos.GreeterGrpcKt
import dandelion.net.protos.HelloRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable

class GreeterRCP(uri: Uri): Closeable {
    val responseState = mutableStateOf("")

    val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")
        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.executor(Dispatchers.IO.asExecutor()).build()

    }


    val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)
    suspend fun sayHello(name: String) {
        try {
            val request = HelloRequest.newBuilder().setName(name).build()
            val response = greeter.sayHello(request)
            responseState.value = response.message
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
        }
    }

    override fun close() {
        channel.shutdownNow()
    }
}