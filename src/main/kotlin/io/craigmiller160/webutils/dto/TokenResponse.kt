package io.craigmiller160.webutils.dto

data class TokenResponse (
        val accessToken: String,
        val refreshToken: String,
        val tokenId: String
)
