package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpHeaderRedactionPolicyTest {

    @Test
    fun sensitiveHeadersAreMatchedCaseInsensitively() {
        assertTrue(HttpHeaderRedactionPolicy.isSensitiveHeader("authorization"))
        assertTrue(HttpHeaderRedactionPolicy.isSensitiveHeader("X-Request-Signature"))
        assertFalse(HttpHeaderRedactionPolicy.isSensitiveHeader("Accept-Language"))
    }
}
