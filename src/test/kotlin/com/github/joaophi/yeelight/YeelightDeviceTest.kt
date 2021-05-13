package com.github.joaophi.yeelight

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class MockSocket : Socket() {
    private lateinit var _request: String
    fun setRequest(request: String) {
        _request = request
    }

    private lateinit var _response: String
    fun setResponse(response: String) {
        _response = response
    }

    private var done = false
    override fun getInputStream() = object : InputStream() {
        private var index = 0

        override fun read(): Int {
            if (index == _response.length) return -1
            return _response[index++].code
        }

        override fun available(): Int {
            return if (done) _response.length - index else 0
        }
    }

    override fun getOutputStream() = object : OutputStream() {
        private var index = 0

        override fun write(b: Int) {
            if (_request[index++].code != b)
                throw Exception("erro")
            done = index == _request.length
        }
    }
}

@ExperimentalCoroutinesApi
internal class YeelightDeviceTest {
    private lateinit var socket: MockSocket
    private lateinit var yeelightDevice: YeelightDevice

    @BeforeEach
    fun setUp() {
        socket = MockSocket()
        yeelightDevice = YeelightDevice(socket)
    }

    @AfterEach
    fun tearDown() {
        yeelightDevice.close()
        socket.close()
    }

    @Test
    fun `send toggle should return true`() = runBlocking {
        socket.setRequest("{\"id\":0,\"method\":\"toggle\",\"params\":[]}\r\n")
        socket.setResponse("{\"id\":0,\"result\":[\"ok\"]}")

        val command = Command.Toggle
        val result = yeelightDevice.sendCommand(command)

        assertThat(result).isTrue()
    }
}