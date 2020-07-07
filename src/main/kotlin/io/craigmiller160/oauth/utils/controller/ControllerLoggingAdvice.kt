package io.craigmiller160.oauth.utils.controller

import com.sun.org.slf4j.internal.LoggerFactory
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ControllerLoggingAdvice {

    private val logger = LoggerFactory.getLogger(ControllerLoggingAdvice::class.java)

    private fun getRequest() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
    private fun getResponse() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).response

    private fun buildPath(request: HttpServletRequest): String {
        return "${handleNull(request.contextPath)}${handleNull(request.servletPath)}${handleNull(request.pathInfo)}?${handleNull(request.queryString)}"
    }

    private fun handleNull(text: String?) = text ?: ""

    @Pointcut("execution(public * io.craigmiller160.*.controller.*Controller.*(..))")
    fun controllerPublicMethods() { }

    private fun getResponseStatus(result: Any?, joinPoint: JoinPoint): Int {
        if (result is ResponseEntity<*>) {
            return result.statusCode.value()
        }

        val responseArg = joinPoint.args
                .find { arg -> arg is HttpServletResponse } as HttpServletResponse?
        if (responseArg != null) {
            return responseArg.status
        }

        return 200
    }

    @Before("controllerPublicMethods()")
    fun logRequest(joinPoint: JoinPoint) {
        val request = getRequest()
        val path = buildPath(request)
        val method = request.method
        logger.debug("Request: $method $path = ${joinPoint.signature.name}()")
    }

    @AfterReturning("controllerPublicMethods()", returning = "result")
    fun logResponseAfterReturning(joinPoint: JoinPoint, result: Any?) {
        val request = getRequest()
        val status = getResponseStatus(result, joinPoint)
        val path = buildPath(request)
        val method = request.method
        logger.debug("Response $status: $method $path = ${joinPoint.signature.name}()")
    }

    @AfterThrowing("controllerPublicMethods()", throwing = "throwing")
    fun logResponseAfterThrowing(joinPoint: JoinPoint, throwing: Throwable) {
        val request = getRequest()
        val path = buildPath(request)
        val method = request.method
        logger.error("Response Error: $method $path = ${joinPoint.signature.name}()", throwing)
    }

}
