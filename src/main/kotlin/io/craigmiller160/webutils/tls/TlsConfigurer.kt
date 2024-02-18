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

package io.craigmiller160.webutils.tls

import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object TlsConfigurer {

    fun configureTlsTrustStore(path: String, type: String, password: String, disableHostnameCheck: Boolean = false) {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

        val trustStore = KeyStore.getInstance(type)
        val trustStoreStream = TlsConfigurer::class.java.classLoader.getResourceAsStream(path)
        trustStore.load(trustStoreStream, password.toCharArray())

        trustManagerFactory.init(trustStore)

        val trustManagers = trustManagerFactory.trustManagers
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
        SSLContext.setDefault(sslContext)

        if (disableHostnameCheck) {
            HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())
        }
    }

}
