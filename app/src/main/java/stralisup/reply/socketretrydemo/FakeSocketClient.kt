package stralisup.reply.socketretrydemo

import kotlinx.coroutines.*

interface SocketListener {
    fun onMessage(message: String)
    fun onFailure()
}

class FakeSocketClient {

    private var socketJob: Job? = null

    fun newWebSocket(listener: SocketListener) {
        socketJob = CoroutineScope(Dispatchers.Default).launch {
            var dataCount = 0
            while (dataCount < 10) {
                delay(1000)
                listener.onMessage("data $dataCount")
                dataCount++
            }
            delay(2000)
            listener.onFailure()
            close()
        }
    }

    fun close() {
        socketJob?.cancel()
        socketJob = null
    }

}