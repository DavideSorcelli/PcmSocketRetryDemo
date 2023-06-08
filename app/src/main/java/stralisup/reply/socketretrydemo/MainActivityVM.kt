package stralisup.reply.socketretrydemo


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MainActivityVM : ViewModel() {

    init {
        Log.d("VM", "init")
        retryingSubscribe().onEach {
            Log.d("VM", "socket emit: $it")
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun retryingSubscribe(): Flow<Result<String>> {
        return Service().subscribe().transformLatest { result ->
            emit(
                result.fold(
                    onSuccess = {
                        Result.success(it)
                    },
                    onFailure = {
                        throw RuntimeException("retry trigger")
                    }
                )
            )
        }.retryWhen { cause, attempt ->
            (cause is RuntimeException).also {
                if (it) {
                    Log.d("VM", "Need to retry, attempt: $attempt")
                    emit(Result.failure(Throwable("RETRYING")))
                    delay(5000)
                }
            }
        }.onCompletion {
            Log.d("VM", "onCompletion, throwable: $it")
        }
    }

}