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
import io.craigmiller160.webutils.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

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
