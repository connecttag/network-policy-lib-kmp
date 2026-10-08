package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExternalUriPolicyTest {
    @Test
    fun kindOfRecognizesSupportedSchemes() {
        assertEquals(ExternalUriKind.Http, ExternalUriPolicy.kindOf("https://example.com"))
        assertEquals(ExternalUriKind.Geo, ExternalUriPolicy.kindOf("geo:15,44"))
        assertEquals(ExternalUriKind.MailTo, ExternalUriPolicy.kindOf("mailto:hello@example.com"))
        assertEquals(ExternalUriKind.Tel, ExternalUriPolicy.kindOf("tel:+967700000000"))
        assertEquals(ExternalUriKind.Sms, ExternalUriPolicy.kindOf("sms:+967700000000"))
        assertEquals(ExternalUriKind.Sms, ExternalUriPolicy.kindOf("smsto:+967700000000"))
        assertEquals(ExternalUriKind.Market, ExternalUriPolicy.kindOf("market://details?id=com.example"))
    }

    @Test
    fun kindOfTrimsInputAndRejectsControlCharacters() {
        assertEquals(ExternalUriKind.Geo, ExternalUriPolicy.kindOf(" geo:15,44 "))
        assertNull(ExternalUriPolicy.kindOf("geo:15,44\nhttps://example.com"))
    }

    @Test
    fun supportedAbsoluteUriRejectsUnsupportedSchemes() {
        assertFalse(ExternalUriPolicy.isSupportedAbsoluteUri("relative/path"))
    }

    @Test
    fun normalizeSupportedAbsoluteUriReturnsTrimmedSupportedUri() {
        assertEquals("tel:+967700000000", ExternalUriPolicy.normalizeSupportedAbsoluteUri(" tel:+967700000000 "))
        assertNull(ExternalUriPolicy.normalizeSupportedAbsoluteUri("ftp://example.com"))
    }

    @Test
    fun schemeHelpersUseTheSharedKindResolver() {
        assertTrue(ExternalUriPolicy.isGeoUri("geo:15,44"))
        assertTrue(ExternalUriPolicy.isMailToUri("mailto:hello@example.com"))
        assertTrue(ExternalUriPolicy.isTelUri("tel:+967700000000"))
        assertTrue(ExternalUriPolicy.isSmsUri("sms:+967700000000"))
        assertTrue(ExternalUriPolicy.isMarketUri("market://details?id=com.example"))
    }
}
