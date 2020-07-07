package io.craigmiller160.webutils.controller

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.AccessDeniedException

@ExtendWith(MockitoExtension::class)
class ErrorControllerAdviceTest {

    @Mock
    private lateinit var req: HttpServletRequest

    private val errorControllerAdvice = ErrorControllerAdvice()

    @Test
    fun test_exception_withAnnotation() {
        Mockito.`when`(req.requestURI).thenReturn("uri")
        val ex = UnsupportedGrantTypeException("message")

        val error = errorControllerAdvice.exception(req, ex)
        Assertions.assertEquals(400, error.statusCodeValue)
        MatcherAssert.assertThat(error.body, CoreMatchers.allOf(
                Matchers.hasProperty("status", CoreMatchers.equalTo(400)),
                Matchers.hasProperty("error", CoreMatchers.equalTo("Bad Request")),
                Matchers.hasProperty("message", CoreMatchers.equalTo("Unsupported OAuth2 Grant Type - message")),
                Matchers.hasProperty("timestamp", CoreMatchers.notNullValue()),
                Matchers.hasProperty("path", CoreMatchers.equalTo("uri"))
        ))
    }

    @Test
    fun test_exception() {
        Mockito.`when`(req.requestURI).thenReturn("uri")
        val ex = Exception("message")

        val error = errorControllerAdvice.exception(req, ex)
        Assertions.assertEquals(500, error.statusCodeValue)
        MatcherAssert.assertThat(error.body, CoreMatchers.allOf(
                Matchers.hasProperty("status", CoreMatchers.equalTo(500)),
                Matchers.hasProperty("error", CoreMatchers.equalTo("Internal Server Error")),
                Matchers.hasProperty("message", CoreMatchers.equalTo("Error - message")),
                Matchers.hasProperty("timestamp", CoreMatchers.notNullValue()),
                Matchers.hasProperty("path", CoreMatchers.equalTo("uri"))
        ))
    }

    @Test
    fun test_accessDeniedException() {
        Mockito.`when`(req.requestURI).thenReturn("uri")
        val ex = Mockito.mock(AccessDeniedException::class.java)
        Mockito.`when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.accessDeniedException(req, ex)
        Assertions.assertEquals(403, error.statusCodeValue)
        MatcherAssert.assertThat(error.body, CoreMatchers.allOf(
                Matchers.hasProperty("status", CoreMatchers.equalTo(403)),
                Matchers.hasProperty("error", CoreMatchers.equalTo("Access Denied")),
                Matchers.hasProperty("message", CoreMatchers.equalTo("message")),
                Matchers.hasProperty("timestamp", CoreMatchers.notNullValue()),
                Matchers.hasProperty("path", CoreMatchers.equalTo("uri"))
        ))
    }

    @Test
    fun test_mediaTypeNotSupportedException() {
        Mockito.`when`(req.requestURI).thenReturn("uri")
        val ex = Mockito.mock(HttpMediaTypeNotSupportedException::class.java)
        Mockito.`when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.mediaTypeNotSupportedException(req, ex)
        Assertions.assertEquals(415, error.statusCodeValue)
        MatcherAssert.assertThat(error.body, CoreMatchers.allOf(
                Matchers.hasProperty("status", CoreMatchers.equalTo(415)),
                Matchers.hasProperty("error", CoreMatchers.equalTo("Unsupported Media Type")),
                Matchers.hasProperty("message", CoreMatchers.equalTo("message")),
                Matchers.hasProperty("timestamp", CoreMatchers.notNullValue()),
                Matchers.hasProperty("path", CoreMatchers.equalTo("uri"))
        ))
    }

    @Test
    fun test_methodNotSupportedException() {
        Mockito.`when`(req.requestURI).thenReturn("uri")
        val ex = Mockito.mock(HttpRequestMethodNotSupportedException::class.java)
        Mockito.`when`(ex.message).thenReturn("message")

        val error = errorControllerAdvice.methodNotSupportedException(req, ex)
        Assertions.assertEquals(405, error.statusCodeValue)
        MatcherAssert.assertThat(error.body, CoreMatchers.allOf(
                Matchers.hasProperty("status", CoreMatchers.equalTo(405)),
                Matchers.hasProperty("error", CoreMatchers.equalTo("Method Not Allowed")),
                Matchers.hasProperty("message", CoreMatchers.equalTo("message")),
                Matchers.hasProperty("timestamp", CoreMatchers.notNullValue()),
                Matchers.hasProperty("path", CoreMatchers.equalTo("uri"))
        ))
    }

}
