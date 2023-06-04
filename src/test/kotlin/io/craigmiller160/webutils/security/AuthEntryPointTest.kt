/*
 * web-utils
 * Copyright (C) 2020 Craig Miller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.webutils.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.AuthenticationException
import java.io.PrintWriter
import java.io.StringWriter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
class AuthEntryPointTest {

    @Spy
    private val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())

    @Mock
    private lateinit var req: HttpServletRequest

    @Mock
    private lateinit var res: HttpServletResponse

    @Mock
    private lateinit var authEx: AuthenticationException

    @InjectMocks
    private lateinit var authEntryPoint: AuthEntryPoint

    private lateinit var writer: StringWriter

    @BeforeEach
    fun setup() {
        Mockito.`when`(req.requestURI)
                .thenReturn("requestURI")
        Mockito.`when`(authEx.message)
                .thenReturn("message")

        writer = StringWriter()
        Mockito.`when`(res.writer)
                .thenReturn(PrintWriter(writer))
    }

    @Test
    fun test_alreadyErrorStatus() {
        Mockito.`when`(res.status)
                .thenReturn(500)
        authEntryPoint.commence(req, res, authEx)

        val result = objectMapper.readValue(writer.toString(), Map::class.java)
        Assertions.assertEquals(500, result["status"])
        Assertions.assertEquals("Internal Server Error", result["error"])
        Assertions.assertEquals("message", result["message"])
        Assertions.assertEquals("requestURI", result["path"])
    }

    @Test
    fun test_setUnauthorized() {
        Mockito.`when`(res.status)
                .thenReturn(200)
        authEntryPoint.commence(req, res, authEx)

        val result = objectMapper.readValue(writer.toString(), Map::class.java)
        Assertions.assertEquals(401, result["status"])
        Assertions.assertEquals("Unauthorized", result["error"])
        Assertions.assertEquals("message", result["message"])
        Assertions.assertEquals("requestURI", result["path"])
    }

}
