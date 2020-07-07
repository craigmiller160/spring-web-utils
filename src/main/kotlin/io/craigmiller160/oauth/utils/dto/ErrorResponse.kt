package io.craigmiller160.oauth.utils.dto

import java.time.LocalDateTime

data class ErrorResponse (
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int = 0,
        val error: String = "",
        val message: String = "",
        val path: String = ""
)
