package org.valkyrienskies.eureka

object IslandConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client {

    }

    class Server {
        var fastVoid = false
    }
}
