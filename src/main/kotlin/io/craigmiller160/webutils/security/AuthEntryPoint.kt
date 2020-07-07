package io.craigmiller160.webutils.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.webutils.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthEntryPoint (
        private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(req: HttpServletRequest?, res: HttpServletResponse?, ex: AuthenticationException?) {
        val status = res?.let {
            if (it.status >= 400) HttpStatus.valueOf(it.status) else HttpStatus.UNAUTHORIZED
        } ?: HttpStatus.UNAUTHORIZED

        val error = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = ex?.message ?: "",
                path = req?.requestURI ?: ""
        )
        val errorPayload = objectMapper.writeValueAsString(error)

        res?.apply {
            this.status = status.value()
            addHeader("Content-Type", "application/json")
            writer?.use { it.write(errorPayload) }
        }
    }

}
