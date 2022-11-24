import dandelion.net.kotlingrpc.Platform

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}