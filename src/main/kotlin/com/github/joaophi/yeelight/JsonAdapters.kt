package com.github.joaophi.yeelight

import com.squareup.moshi.*
import com.squareup.moshi.internal.Util

object ErrorJsonAdapter {
    private val options: JsonReader.Options = JsonReader.Options.of("code", "message")

    @FromJson
    fun fromJson(reader: JsonReader): Error {
        var code: Int? = null
        var message: String? = null

        reader.beginObject()
        while (reader.hasNext()) when (reader.selectName(options)) {
            0 -> code = reader.nextInt()
            1 -> message = reader.nextString()
            -1 -> {
                // Unknown name, skip it.
                reader.skipName()
                reader.skipValue()
            }
        }
        reader.endObject()

        return Error(
            code ?: throw Util.unexpectedNull("code", "code", reader),
            message ?: throw Util.unexpectedNull("message", "message", reader)
        )
    }

    @ToJson
    fun toJson(jsonWriter: JsonWriter, response: Error) = Unit
}

object ResponseJsonAdapter {
    private val options: JsonReader.Options = JsonReader.Options.of("id", "result", "error", "method", "params")

    @FromJson
    fun fromJson(
        reader: JsonReader,
        listAdapter: JsonAdapter<List<Any>>,
        errorAdapter: JsonAdapter<Error>,
        mapAdapter: JsonAdapter<Map<String, Any>>,
    ): Response {
        var id: Int? = null
        var result: List<Any>? = null
        var error: Error? = null
        var method: String? = null
        var params: Map<String, Any>? = null

        reader.beginObject()
        while (reader.hasNext()) when (reader.selectName(options)) {
            0 -> id = reader.nextInt()
            1 -> result = listAdapter.fromJson(reader) ?: throw Util.unexpectedNull("result", "result", reader)
            2 -> error = errorAdapter.fromJson(reader) ?: throw Util.unexpectedNull("error", "error", reader)
            3 -> method = reader.nextString()
            4 -> params = mapAdapter.fromJson(reader) ?: throw Util.unexpectedNull("params", "params", reader)
            -1 -> {
                // Unknown name, skip it.
                reader.skipName()
                reader.skipValue()
            }
        }
        reader.endObject()

        return when {
            id != null -> when {
                result != null -> SuccessResult(id, result)
                error != null -> ErrorResult(id, error)
                else -> throw Util.missingProperty("result|error", "result|error", reader)
            }
            else -> Notification(
                method ?: throw Util.missingProperty("method", "method", reader),
                params ?: throw Util.missingProperty("params", "params", reader),
            )
        }
    }

    @ToJson
    fun toJson(jsonWriter: JsonWriter, response: Response) = Unit
}