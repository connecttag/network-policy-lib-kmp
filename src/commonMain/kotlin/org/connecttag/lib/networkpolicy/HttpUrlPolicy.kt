package org.connecttag.lib.networkpolicy

object HttpUrlPolicy {
    fun isHttpUrl(value: String?): Boolean {
        val cleaned = value.cleanUrlCandidate() ?: return false
        if (cleaned.hasUriControlCharacters()) return false
        return cleaned.startsWith("http://", ignoreCase = true) ||
                cleaned.startsWith("https://", ignoreCase = true)
    }

    fun isHttpsUrl(value: String?): Boolean {
        val cleaned = value.cleanUrlCandidate() ?: return false
        if (cleaned.hasUriControlCharacters()) return false
        return cleaned.startsWith("https://", ignoreCase = true)
    }

    fun normalizeHttpUrl(value: String?): String? {
        val cleaned = value.cleanUrlCandidate() ?: return null
        return cleaned.takeIf { isHttpUrl(it) }
    }

    private fun String?.cleanUrlCandidate(): String? {
        val raw = this ?: return null
        if (raw.hasUriControlCharacters()) return null
        return raw.trim().takeIf { it.isNotEmpty() }
    }
}

internal fun String.hasUriControlCharacters(): Boolean =
    any { character -> character.code < 0x20 || character.code == 0x7F }
