package stralisup.reply.socketretrydemo

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Service {

    private val client = FakeSocketClient()

    fun subscribe(): Flow<Result<String>> = callbackFlow {
        client.newWebSocket(
            listener = object : SocketListener {
                override fun onMessage(message: String) {
                    trySend(Result.success(message))
                }

                override fun onFailure() {
                    trySend(Result.failure(Throwable("Socket onFailure")))
                }
            }
        )

        awaitClose {
            Log.d("Service", "awaitClose")
            client.close()
        }

    }

}