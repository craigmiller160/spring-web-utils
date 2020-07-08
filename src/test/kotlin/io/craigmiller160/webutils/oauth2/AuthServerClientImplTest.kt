package io.craigmiller160.webutils.oauth2

import io.craigmiller160.webutils.dto.TokenResponse
import io.craigmiller160.webutils.exception.InvalidResponseBodyException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@ExtendWith(MockitoExtension::class)
class AuthServerClientImplTest {

    private val host = "host"
    private val path = "path"
    private val key = "key"
    private val secret = "secret"
    private val redirectUri = "redirectUri"
    private val authHeader = "Basic a2V5OnNlY3JldA=="
    private val response = TokenResponse("access", "refresh", "id")

    @Mock
    private lateinit var restTemplate: RestTemplate

    @Mock
    private lateinit var oAuthConfig: OAuthConfig

    @InjectMocks
    private lateinit var authServerClient: AuthServerClientImpl

    @BeforeEach
    fun setup() {
        Mockito.`when`(oAuthConfig.authServerHost)
                .thenReturn(host)
        Mockito.`when`(oAuthConfig.tokenPath)
                .thenReturn(path)
        Mockito.`when`(oAuthConfig.clientKey)
                .thenReturn(key)
        Mockito.`when`(oAuthConfig.clientSecret)
                .thenReturn(secret)
    }

    @Test
    fun test_authenticateAuthCode() {
        Mockito.`when`(oAuthConfig.authCodeRedirectUri)
                .thenReturn(redirectUri)

        val authCode = "DERFG"
        val entityCaptor = ArgumentCaptor.forClass(HttpEntity::class.java)

        Mockito.`when`(restTemplate.exchange(
                ArgumentMatchers.eq("$host$path"),
                ArgumentMatchers.eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(TokenResponse::class.java)
        ))
                .thenReturn(ResponseEntity.ok(response))

        val result = authServerClient.authenticateAuthCode(authCode)
        assertEquals(response, result)

        Assertions.assertEquals(1, entityCaptor.allValues.size)
        val entity = entityCaptor.value

        Assertions.assertEquals(this.authHeader, entity.headers["Authorization"]?.get(0))
        Assertions.assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity.headers["Content-Type"]?.get(0))

        val body = entity.body
        Assertions.assertTrue(body is MultiValueMap<*, *>)
        val map = body as MultiValueMap<String, String>
        Assertions.assertEquals("authorization_code", map["grant_type"]?.get(0))
        Assertions.assertEquals(redirectUri, map["redirect_uri"]?.get(0))
        Assertions.assertEquals(key, map["client_id"]?.get(0))
        Assertions.assertEquals(authCode, map["code"]?.get(0))
    }

    @Test
    fun test_authenticateRefreshToken_invalidResponseBody() {
        val refreshToken = "ABCDEFG"

        Mockito.`when`(restTemplate.exchange(
                ArgumentMatchers.eq("$host$path"),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.isA(HttpEntity::class.java),
                eq(TokenResponse::class.java)
        ))
                .thenReturn(ResponseEntity.noContent().build())

        assertThrows<InvalidResponseBodyException> { authServerClient.authenticateRefreshToken(refreshToken) }
    }

    @Test
    fun test_authenticateRefreshToken() {
        val refreshToken = "ABCDEFG"

        val entityCaptor = ArgumentCaptor.forClass(HttpEntity::class.java)

        Mockito.`when`(restTemplate.exchange(
                ArgumentMatchers.eq("$host$path"),
                ArgumentMatchers.eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(TokenResponse::class.java)
        ))
                .thenReturn(ResponseEntity.ok(response))

        val result = authServerClient.authenticateRefreshToken(refreshToken)
        assertEquals(response, result)

        Assertions.assertEquals(1, entityCaptor.allValues.size)
        val entity = entityCaptor.value

        Assertions.assertEquals(this.authHeader, entity.headers["Authorization"]?.get(0))
        Assertions.assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity.headers["Content-Type"]?.get(0))

        val body = entity.body
        Assertions.assertTrue(body is MultiValueMap<*, *>)
        val map = body as MultiValueMap<String, String>
        Assertions.assertEquals("refresh_token", map["grant_type"]?.get(0))
        Assertions.assertEquals(refreshToken, map["refresh_token"]?.get(0))
    }

}
