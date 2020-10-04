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
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class ErrorControllerAdvice {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AccessDeniedException::class)
    fun accessDeniedException(req: HttpServletRequest, ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 403
        val error = ErrorResponse(
                status = status,
                error = "Access Denied",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun mediaTypeNotSupportedException(req: HttpServletRequest, ex: HttpMediaTypeNotSupportedException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 415
        val error = ErrorResponse(
                status = status,
                error = "Unsupported Media Type",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotSupportedException(req: HttpServletRequest, ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 405
        val error = ErrorResponse(
                status = status,
                error = "Method Not Allowed",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(BeanInstantiationException::class)
    fun beanInstantiationException(req: HttpServletRequest, ex: BeanInstantiationException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 400
        val error = ErrorResponse(
                status = status,
                error = "Bad Request",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatchException(req: HttpServletRequest, ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 400
        val error = ErrorResponse(
                status = status,
                error = "Bad Request",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun entityNotFoundException(req: HttpServletRequest, ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val status = 400
        val error = ErrorResponse(
                status = status,
                error = "Bad Request",
                message = ex.message ?: "",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

    @ExceptionHandler(Exception::class)
    fun exception(req: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("", ex)
        val annotation = ex.javaClass.getAnnotation(ResponseStatus::class.java)
        val status = annotation?.code?.value() ?: 500
        val error = ErrorResponse(
                status = status,
                error = annotation?.code?.reasonPhrase ?: "Internal Server Error",
                message = "${annotation?.reason ?: "Error"} - ${ex.message}",
                path = req.requestURI
        )
        return ResponseEntity
                .status(status)
                .body(error)
    }

}
