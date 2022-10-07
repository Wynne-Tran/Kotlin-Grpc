package dandelion.net.kotlingrpc
import android.net.Uri

class Greeting {
    fun android(uri: Uri): GreeterRCP {
        return GreeterRCP(uri);
    }
    fun greeting(): String {
        return "Hello, IOS!"
    }
}
