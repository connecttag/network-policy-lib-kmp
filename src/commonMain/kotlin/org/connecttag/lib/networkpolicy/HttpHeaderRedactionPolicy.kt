package org.connecttag.lib.networkpolicy

object HttpHeaderRedactionPolicy {
    val SENSITIVE_HEADERS: Set<String> = setOf(
        "Authorization",
        "Api-Key",
        "X-Api-Key",
        "Cookie",
        "Set-Cookie",
        "X-Security-Challenge-Token",
        "X-Request-Signature",
        "X-Request-Signature-Key",
        "X-Request-Signature-Timestamp",
        "X-Request-Signature-Nonce",
        "X-Signature",
        "X-Signature-Timestamp",
        "X-Signature-Nonce",
        "X-Signature-Version",
        "X-Content-SHA256",
        "X-Session-Id",
    )

    fun isSensitiveHeader(name: String): Boolean {
        return SENSITIVE_HEADERS.any { it.equals(name.trim(), ignoreCase = true) }
    }
}
