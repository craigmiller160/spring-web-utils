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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
