package com.github.joaophi.yeelight

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okio.buffer
import okio.sink
import okio.source
import java.io.Closeable
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

class YeelightDevice(
    private val socket: Socket,
    private val musicMode: Boolean = false,
) : Closeable {
    constructor(host: String, port: Int = 55443) : this(Socket(host, port))

    private val id = AtomicInteger()

    private val inputStream = socket.getInputStream()
    private val source = inputStream.source().buffer()
    private val sink = socket.sink().buffer()

    private val requestAdapter: JsonAdapter<Request>
    private val responseAdapter: JsonAdapter<Response>

    init {
        val moshi = Moshi.Builder()
            .add(ErrorJsonAdapter)
            .add(ResponseJsonAdapter)
            .build()
        requestAdapter = moshi.adapter(Request::class.java)
        responseAdapter = moshi.adapter(Response::class.java)
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val response = flow {
        while (currentCoroutineContext().isActive) when (inputStream.available()) {
            0 -> yield()
            else -> {
                val response = responseAdapter.fromJson(source) ?: continue
                emit(response)
            }
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed())

    val properties = response
        .filterIsInstance<Notification>()
        .map { notification -> Properties(notification.params) }

    suspend fun <R : Any> sendCommand(command: Command<R>): R =
        withContext(scope.coroutineContext) {
            val id = id.getAndIncrement()
            val request = Request(id, command.method, command.params)
            requestAdapter.toJson(sink, request)
            sink.writeString("\r\n", Charsets.UTF_8)
            sink.flush()

            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            when {
                musicMode -> when (command) {
                    is Command.CronGet -> emptyList<Cron>()
                    is Command.GetProp -> Properties(emptyMap())
                    else -> true
                } as R
                else -> {
                    val response = response
                        .filterIsInstance<Result>()
                        .first { it.id == id }
                    when (response) {
                        is SuccessResult -> command.parseResult(response.result)
                        is ErrorResult -> throw response.error
                    }
                }
            }
        }

    override fun close() {
        scope.cancel()
        socket.close()
    }
}