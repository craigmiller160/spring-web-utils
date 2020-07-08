package io.craigmiller160.webutils.oauth2

import io.craigmiller160.webutils.dto.TokenResponse

interface AuthServerClient {
    fun authenticateAuthCode(code: String): TokenResponse
    fun authenticateRefreshToken(refreshToken: String): TokenResponse
}
