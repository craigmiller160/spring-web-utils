package io.craigmiller160.webutils.security

import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object TlsConfigurer {

    fun configureTlsTrustStore(path: String, type: String, password: String) {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

        val trustStore = KeyStore.getInstance(type)
        val trustStoreStream = TlsConfigurer::class.java.classLoader.getResourceAsStream(path)
        trustStore.load(trustStoreStream, password.toCharArray())

        trustManagerFactory.init(trustStore)

        val trustManagers = trustManagerFactory.trustManagers
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
        SSLContext.setDefault(sslContext)

        HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())
    }

}
