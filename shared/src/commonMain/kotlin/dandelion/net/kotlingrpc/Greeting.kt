package dandelion.net.kotlingrpc

//class Greeting {
//    fun greeting(): String {
//        return "Hello, ${Platform().platform}!"
//    }
//}

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dandelion.net.protos.GreeterGrpcKt
import dandelion.net.protos.HelloRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import java.io.Closeable

class Greeting {
    @Composable
    fun Greeter(greeterRCP: GreeterRCP) {
        val scope = rememberCoroutineScope()
        val nameState = remember { mutableStateOf(TextFieldValue()) }
        Column(
            Modifier.fillMaxWidth().fillMaxHeight(),
            Arrangement.Top,
            Alignment.CenterHorizontally
        ) {
            Text("Input Year", modifier = Modifier.padding(top = 10.dp))
            OutlinedTextField(nameState.value, { nameState.value = it })
            Button(
                { scope.launch { greeterRCP.sayHello(nameState.value.text) } },
                Modifier.padding(10.dp)
            ) {
                Text("Request")
            }
            if (greeterRCP.responseState.value.isNotEmpty()) {
                Text(
                    "HelloServer Response",
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(greeterRCP.responseState.value)
            }
        }
    }
}
