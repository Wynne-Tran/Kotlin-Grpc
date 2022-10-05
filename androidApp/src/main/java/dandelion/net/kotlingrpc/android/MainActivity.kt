package dandelion.net.kotlingrpc.android
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import dandelion.net.kotlingrpc.GreeterRCP
import dandelion.net.kotlingrpc.Greeting


class MainActivity : AppCompatActivity() {
    private val uri by lazy { Uri.parse("http://10.0.2.2:8080") }
    private val greeterService by lazy { GreeterRCP(uri) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Greeting().Greeter(greeterService)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        greeterService.close()
    }
}
//
//class GreeterRCP(uri: Uri) : Closeable {
//    val responseState = mutableStateOf("")
//
//    private val channel = let {
//        println("Connecting to ${uri.host}:${uri.port}")
//        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
//        if (uri.scheme == "https") {
//            builder.useTransportSecurity()
//        } else {
//            builder.usePlaintext()
//        }
//        builder.executor(Dispatchers.IO.asExecutor()).build()
//    }
//
//
//    private val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)
//    suspend fun sayHello(name: String) {
//        try {
//            val request = HelloRequest.newBuilder().setName(name).build()
//            val response = greeter.sayHello(request)
//            responseState.value = response.message
//        } catch (e: Exception) {
//            responseState.value = e.message ?: "Unknown Error"
//            e.printStackTrace()
//        }
//    }
//    override fun close() {
//        channel.shutdownNow()
//    }
//}
//
//@Composable
//fun Greeter(greeterRCP: GreeterRCP) {
//    val scope = rememberCoroutineScope()
//    val nameState = remember { mutableStateOf(TextFieldValue()) }
//    Column(Modifier.fillMaxWidth().fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally) {
//        Text(stringResource(R.string.name_hint), modifier = Modifier.padding(top = 10.dp))
//        OutlinedTextField(nameState.value, { nameState.value = it })
//        Button({ scope.launch { greeterRCP.sayHello(nameState.value.text) } }, Modifier.padding(10.dp)) {
//            Text(stringResource(R.string.send_request))
//        }
//        if (greeterRCP.responseState.value.isNotEmpty()) {
//            Text(stringResource(R.string.server_response), modifier = Modifier.padding(top = 10.dp))
//            Text(greeterRCP.responseState.value)
//        }
//    }
//}