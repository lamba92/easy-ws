package it.lamba

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.filterNotNull
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch

fun getClient(username: String, password: String) = HttpClient(Js) {
    install(WebSockets)
    install(Auth) {
        basic {
            this.username = username
            this.password = password
        }
    }
}

interface OnMessageReceivedListener {
    fun onMessageReceived(message: String)
}

fun closeSession(wrapper: WSSessionWrapper){
    GlobalScope.launch {
        wrapper.session?.close()
    }
}

data class WSSessionWrapper(var session: DefaultClientWebSocketSession? = null)

fun webSocket(
    client: HttpClient,
    host: String,
    port: Int,
    path: String,
    listener: OnMessageReceivedListener,
    sessionGetter: WSSessionWrapper,
    method: HttpMethod = HttpMethod.Get
) {
    GlobalScope.launch {
        client.webSocket(method, host, port, path) {
            sessionGetter.session = this
            for(message in incoming.map { it as? Frame.Text }.filterNotNull()){
                listener.onMessageReceived(message.readText())
            }
        }
    }
}