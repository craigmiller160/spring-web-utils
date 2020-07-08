package io.craigmiller160.webutils.oauth2

import com.nimbusds.jose.jwk.JWKSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.spy

class OAuthConfigTest {

    private val oAuthConfig = OAuthConfig(jwkPath = "/path")

    @Test
    fun test_loadJWKSet_firstTrySuccess() {
        val spyConfig = spy(oAuthConfig)

        doReturn(1L)
                .`when`(spyConfig)
                .getBaseWait()

        doReturn(Mockito.mock(JWKSet::class.java))
                .`when`(spyConfig)
                .loadJWKSet()

        spyConfig.tryToLoadJWKSet()
        Assertions.assertNotNull(spyConfig.jwkSet)
    }

    @Test
    fun test_loadJWKSet_secondTrySuccess() {
        val spyConfig = spy(oAuthConfig)

        doReturn(1L)
                .`when`(spyConfig)
                .getBaseWait()

        doThrow(RuntimeException("Hello"))
                .doReturn(Mockito.mock(JWKSet::class.java))
                .`when`(spyConfig)
                .loadJWKSet()

        spyConfig.tryToLoadJWKSet()
        Assertions.assertNotNull(spyConfig.jwkSet)
    }

    @Test
    fun test_loadJWKSet_failure() {
        val spyConfig = spy(oAuthConfig)

        doReturn(1L)
                .`when`(spyConfig)
                .getBaseWait()

        doThrow(RuntimeException("Hello"))
                .`when`(spyConfig)
                .loadJWKSet()

        val ex = assertThrows<java.lang.RuntimeException> { spyConfig.tryToLoadJWKSet() }
        Assertions.assertEquals("Failed to load JWKSet", ex.message)
        val ex2 = assertThrows<UninitializedPropertyAccessException> { spyConfig.jwkSet }
    }

}
