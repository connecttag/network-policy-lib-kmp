package org.connecttag.lib.networkpolicy

enum class ExternalUriKind {
    Http,
    Geo,
    MailTo,
    Tel,
    Sms,
    Market,
}

object ExternalUriPolicy {
    fun kindOf(value: String?): ExternalUriKind? {
        val cleaned = value.cleanExternalUriCandidate() ?: return null
        if (cleaned.hasUriControlCharacters()) return null
        return when {
            HttpUrlPolicy.isHttpUrl(cleaned) -> ExternalUriKind.Http
            cleaned.startsWith("geo:", ignoreCase = true) -> ExternalUriKind.Geo
            cleaned.startsWith("mailto:", ignoreCase = true) -> ExternalUriKind.MailTo
            cleaned.startsWith("tel:", ignoreCase = true) -> ExternalUriKind.Tel
            cleaned.startsWith("sms:", ignoreCase = true) ||
                    cleaned.startsWith("smsto:", ignoreCase = true) -> ExternalUriKind.Sms
            cleaned.startsWith("market:", ignoreCase = true) -> ExternalUriKind.Market
            else -> null
        }
    }

    fun isSupportedAbsoluteUri(value: String?): Boolean =
        kindOf(value) != null

    fun normalizeSupportedAbsoluteUri(value: String?): String? {
        val cleaned = value.cleanExternalUriCandidate() ?: return null
        return cleaned.takeIf { isSupportedAbsoluteUri(it) }
    }

    fun isGeoUri(value: String?): Boolean =
        kindOf(value) == ExternalUriKind.Geo

    fun isMailToUri(value: String?): Boolean =
        kindOf(value) == ExternalUriKind.MailTo

    fun isTelUri(value: String?): Boolean =
        kindOf(value) == ExternalUriKind.Tel

    fun isSmsUri(value: String?): Boolean =
        kindOf(value) == ExternalUriKind.Sms

    fun isMarketUri(value: String?): Boolean =
        kindOf(value) == ExternalUriKind.Market

    private fun String?.cleanExternalUriCandidate(): String? {
        val raw = this ?: return null
        if (raw.hasUriControlCharacters()) return null
        return raw.trim().takeIf { it.isNotEmpty() }
    }
}
