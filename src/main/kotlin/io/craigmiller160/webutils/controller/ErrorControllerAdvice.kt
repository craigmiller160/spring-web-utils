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

import io.craigmiller160.webutils.dto.ErrorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanInstantiationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest

@ControllerAdvice
class ErrorControllerAdvice {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AccessDeniedException::class)
    fun accessDeniedException(req: HttpServletRequest, ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun mediaTypeNotSupportedException(req: HttpServletRequest, ex: HttpMediaTypeNotSupportedException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotSupportedException(req: HttpServletRequest, ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(BeanInstantiationException::class)
    fun beanInstantiationException(req: HttpServletRequest, ex: BeanInstantiationException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatchException(req: HttpServletRequest, ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun entityNotFoundException(req: HttpServletRequest, ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        return handleException(req, ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun exception(req: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        val annotation: ResponseStatus? = ex.javaClass.getAnnotation(ResponseStatus::class.java)
        val status = annotation?.code ?: HttpStatus.INTERNAL_SERVER_ERROR
        val message = annotation?.reason

        return handleException(req, ex, status, message)
    }

    private fun handleException(req: HttpServletRequest, ex: Exception, status: HttpStatus, message: String? = null): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val errorMessage = "${message ?: "Error"} - ${ex.message ?: ""}"
        val error = ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = errorMessage,
                path = req.requestURI,
                method = req.method
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

}
