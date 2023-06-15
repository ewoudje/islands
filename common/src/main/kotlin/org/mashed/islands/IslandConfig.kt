package org.mashed.islands

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
