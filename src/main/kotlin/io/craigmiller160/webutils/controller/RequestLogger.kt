package io.craigmiller160.webutils.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This needs to be added to an implementation of WebMvcConfigurer
 *
 * override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestLogger)
            .addPathPatterns("/**/**")
    }
 */
@Component
class RequestLogger : HandlerInterceptorAdapter() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uri = request.requestURI
        val method = request.method
        log.debug("Request: $method $uri")
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val uri = request.requestURI
        val method = request.method
        val status = response.status
        log.debug("Response: $status $method $uri")
    }

}
