package dandelion.net.kotlingrpc

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}