package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UriPolicyTest {
    @Test
    fun parseExtractsSchemeHostAndPort() {
        val uri = UriPolicy.parse(" https://example.com:8443/path?q=1 ")

        assertEquals("https", uri?.scheme)
        assertEquals("example.com", uri?.host)
        assertEquals(8443, uri?.port)
        assertEquals(8443, uri?.effectivePort())
    }

    @Test
    fun parseSupportsIpv6Authorities() {
        val uri = UriPolicy.parse("http://[2001:db8::1]:8080/path")

        assertEquals("http", uri?.scheme)
        assertEquals("2001:db8::1", uri?.host)
        assertEquals(8080, uri?.port)
        assertEquals("/path", uri?.path)
    }

    @Test
    fun parseExtractsPathWithoutQueryOrFragment() {
        val uri = UriPolicy.parse("https://example.com/maps?query=1#section")

        assertEquals("/maps", uri?.path)
        assertEquals("https", uri?.scheme)
        assertEquals("example.com", uri?.host)
        assertNull(uri?.port)
    }

    @Test
    fun parseExtractsRootPathWhenMissingPath() {
        val uri = UriPolicy.parse("https://example.com")

        assertEquals("/", uri?.path)
        assertEquals("https", uri?.scheme)
        assertEquals("example.com", uri?.host)
    }

    @Test
    fun parseRejectsWhitespaceAndInvalidPorts() {
        assertNull(UriPolicy.parse("https://example.com/a b"))
        assertNull(UriPolicy.parse("https://example.com:80abc/path")?.host)
        assertNull(UriPolicy.parse("https://example.com:999999999999999999999/path")?.host)
    }

    @Test
    fun encodeQueryComponentUsesUrlFormEncoding() {
        assertEquals("Sana%27a+city", UriComponentCodec.encodeQueryComponent("Sana'a city"))
        assertEquals("%D8%B5%D9%86%D8%B9%D8%A7%D8%A1", UriComponentCodec.encodeQueryComponent("\u0635\u0646\u0639\u0627\u0621"))
    }
}
