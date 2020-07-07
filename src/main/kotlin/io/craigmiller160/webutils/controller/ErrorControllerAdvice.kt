package io.craigmiller160.webutils.controller

import io.craigmiller160.webutils.dto.ErrorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.AccessDeniedException

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
