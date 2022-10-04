package dandelion.net.kotlingrpc.android
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dandelion.net.kotlingrpc.android.R
import dandelion.net.protos.BirthYearRequest
import dandelion.net.protos.CalculateAgeGrpcKt
import dandelion.net.protos.GreeterGrpcKt
import dandelion.net.protos.HelloRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Closeable

class MainActivity : AppCompatActivity() {
    private val uri by lazy { Uri.parse("http://10.0.2.2:8080/") }
    private val greeterService by lazy { GreeterRCP(uri) }

    private val uriB by lazy { Uri.parse("http://10.0.2.2:8089/") }
    private val calculateAgeService by lazy { CalculateAgeRCP(uriB) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Greeter(greeterService, calculateAgeService)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        greeterService.close()
    }
}

// Greeter Service
class GreeterRCP(uri: Uri) : Closeable {
    val responseState = mutableStateOf("")

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")
        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.executor(Dispatchers.IO.asExecutor()).build()
    }


    private val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)
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


//Calculate Age service
class CalculateAgeRCP(uri: Uri) : Closeable {
    val responseState = mutableStateOf("")

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")
        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val getAge  = CalculateAgeGrpcKt.CalculateAgeCoroutineStub(channel)
    suspend fun calculateAge(age: Int) {
        try {
            val request = BirthYearRequest.newBuilder().setBirthYear(age).build()
            val response = getAge.calculateAge(request)
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


//Client side
@Composable
fun Greeter(greeterRCP: GreeterRCP, calculateAgeRCP: CalculateAgeRCP) {
    val scope = rememberCoroutineScope()
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    Column(Modifier.fillMaxWidth().fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally) {
        Text(stringResource(R.string.server_response), modifier = Modifier.padding(top = 10.dp))
        OutlinedTextField(nameState.value, { nameState.value = it })
        Button(
        {
            scope.launch {
                //Hello_Server
                greeterRCP.sayHello(nameState.value.text)
                val getAge = (greeterRCP.responseState.value).split(" ").toTypedArray()
                var resFromHelloServer = 0
                try{
                    resFromHelloServer = getAge[getAge.size - 1].toInt()
                }
                catch(_:NumberFormatException) {
                    resFromHelloServer = -10
                }
                //CalculateAge Server
                calculateAgeRCP.calculateAge(resFromHelloServer)
            }
        }, Modifier.padding(10.dp)) {
            Text(stringResource(R.string.send_request))
        }
        if (greeterRCP.responseState.value.isNotEmpty() && greeterRCP.responseState.value.isNotEmpty()) {
            Text(stringResource(R.string.server_response), modifier = Modifier.padding(top = 10.dp))
            Text(greeterRCP.responseState.value)
            Text(stringResource( R.string.server_response), modifier = Modifier.padding(top = 10.dp))
            Text(calculateAgeRCP.responseState.value)
        }
    }
}