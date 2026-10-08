package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HttpUrlPolicyTest {
    @Test
    fun isHttpUrlAcceptsHttpAndHttpsUrls() {
        assertTrue(HttpUrlPolicy.isHttpUrl("http://example.com"))
        assertTrue(HttpUrlPolicy.isHttpUrl("https://example.com"))
    }

    @Test
    fun isHttpUrlTrimsInputAndIgnoresSchemeCase() {
        assertTrue(HttpUrlPolicy.isHttpUrl(" HTTPS://example.com "))
    }

    @Test
    fun isHttpUrlRejectsBlankNullAndUnsupportedSchemes() {
        assertFalse(HttpUrlPolicy.isHttpUrl(null))
        assertFalse(HttpUrlPolicy.isHttpUrl(" "))
        assertFalse(HttpUrlPolicy.isHttpUrl("ftp://example.com"))
        assertFalse(HttpUrlPolicy.isHttpUrl("/internal/path"))
    }

    @Test
    fun isHttpUrlRejectsControlCharacters() {
        assertFalse(HttpUrlPolicy.isHttpUrl("https://example.com\njavascript:alert(1)"))
        assertFalse(HttpUrlPolicy.isHttpUrl("https://example.com\u0000"))
    }

    @Test
    fun isHttpsUrlAcceptsHttpsOnly() {
        assertTrue(HttpUrlPolicy.isHttpsUrl("https://example.com"))
        assertFalse(HttpUrlPolicy.isHttpsUrl("http://example.com"))
    }

    @Test
    fun normalizeHttpUrlReturnsTrimmedUrlOnlyWhenSupported() {
        assertEquals("https://example.com", HttpUrlPolicy.normalizeHttpUrl(" https://example.com "))
        assertNull(HttpUrlPolicy.normalizeHttpUrl("ftp://example.com"))
        assertNull(HttpUrlPolicy.normalizeHttpUrl("https://example.com\n"))
    }
}
