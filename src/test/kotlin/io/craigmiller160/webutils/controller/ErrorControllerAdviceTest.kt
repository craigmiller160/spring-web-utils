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

package io.craigmiller160.webutils.controller

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.AccessDeniedException

@ExtendWith(MockitoExtension::class)
class ErrorControllerAdviceTest {

    // TODO add more tests for more methods

    @Mock
    private lateinit var req: HttpServletRequest

    private val errorControllerAdvice = ErrorControllerAdvice()

    @Test
    fun test_exception_withAnnotation() {
        `when`(req.requestURI).thenReturn("uri")
        `when`(req.method).thenReturn("GET")
        val ex = UnsupportedGrantTypeException("message")

        val error = errorControllerAdvice.exception(req, ex)
        assertEquals(400, error.statusCodeValue)
        assertThat(error.body, allOf(
                hasProperty("status", equalTo(400)),
                hasProperty("error", equalTo("Bad Request")),
                hasProperty("message", equalTo("Unsupported OAuth2 Grant Type - message")),
                hasProperty("timestamp", notNullValue()),
                hasProperty("path", equalTo("uri"))
        ))
    }

    @Test
    fun test_exception() {
        `when`(req.requestURI).thenReturn("uri")
        `when`(req.method).thenReturn("GET")
        val ex = Exception("message")

        val error = errorControllerAdvice.exception(req, ex)
        assertEquals(500, error.statusCodeValue)
        assertThat(error.body, allOf(
                hasProperty("status", equalTo(500)),
                hasProperty("error", equalTo("Internal Server Error")),
                hasProperty("message", equalTo("Error - message")),
                hasProperty("timestamp", notNullValue()),
                hasProperty("path", equalTo("uri"))
        ))
    }

    @Test
    fun test_accessDeniedException() {
        `when`(req.requestURI).thenReturn("uri")
        `when`(req.method).thenReturn("GET")
        val ex = mock(AccessDeniedException::class.java)
        `when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.accessDeniedException(req, ex)
        assertEquals(403, error.statusCodeValue)
        assertThat(error.body, allOf(
                hasProperty("status", equalTo(403)),
                hasProperty("error", equalTo("Forbidden")),
                hasProperty("message", equalTo("Error - message")),
                hasProperty("timestamp", notNullValue()),
                hasProperty("path", equalTo("uri"))
        ))
    }

    @Test
    fun test_mediaTypeNotSupportedException() {
        `when`(req.requestURI).thenReturn("uri")
        `when`(req.method).thenReturn("GET")
        val ex = mock(HttpMediaTypeNotSupportedException::class.java)
        `when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.mediaTypeNotSupportedException(req, ex)
        assertEquals(415, error.statusCodeValue)
        assertThat(error.body, allOf(
                hasProperty("status", equalTo(415)),
                hasProperty("error", equalTo("Unsupported Media Type")),
                hasProperty("message", equalTo("Error - message")),
                hasProperty("timestamp", notNullValue()),
                hasProperty("path", equalTo("uri"))
        ))
    }

    @Test
    fun test_methodNotSupportedException() {
        `when`(req.requestURI).thenReturn("uri")
        `when`(req.method).thenReturn("GET")
        val ex = mock(HttpRequestMethodNotSupportedException::class.java)
        `when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.methodNotSupportedException(req, ex)
        assertEquals(405, error.statusCodeValue)
        assertThat(error.body, allOf(
                hasProperty("status", equalTo(405)),
                hasProperty("error", equalTo("Method Not Allowed")),
                hasProperty("message", equalTo("Error - message")),
                hasProperty("timestamp", notNullValue()),
                hasProperty("path", equalTo("uri"))
        ))
    }

    @Test
    fun test_beanInstantiationException() {
        TODO("Finish this")
    }

    @Test
    fun test_methodArgumentTypeMismatchException() {
        TODO("Finish this")
    }

    @Test
    fun test_entityNotFoundException() {
        TODO("Finish this")
    }

}
