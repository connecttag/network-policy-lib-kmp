package org.connecttag.lib.networkpolicy

data class UriParts(
    val value: String,
    val scheme: String?,
    val host: String?,
    val port: Int?,
    val path: String?,
) {
    fun effectivePort(): Int? {
        if (port != null && port > 0) return port
        return when (scheme?.lowercase()) {
            "http" -> 80
            "https" -> 443
            else -> null
        }
    }
}

object UriPolicy {
    fun parse(value: String?): UriParts? {
        val cleaned = value.cleanUriCandidate() ?: return null
        val scheme = cleaned.extractScheme()
        val authority = cleaned.extractAuthority(scheme)
        val path = cleaned.extractPath(scheme)
        return UriParts(
            value = cleaned,
            scheme = scheme,
            host = authority?.extractHost(),
            port = authority?.extractPort(),
            path = path,
        )
    }

    fun schemeOf(value: String?): String? =
        parse(value)?.scheme

    fun hostOf(value: String?): String? =
        parse(value)?.host

    fun pathOf(value: String?): String? =
        parse(value)?.path
}

object UriComponentCodec {
    fun encodeQueryComponent(value: String): String {
        val bytes = value.encodeToByteArray()
        return buildString {
            bytes.forEach { byte ->
                val code = byte.toInt() and 0xFF
                when {
                    code == SPACE_CODE -> append('+')
                    code.isUrlFormSafeByte() -> append(code.toChar())
                    else -> {
                        append('%')
                        append(HEX_DIGITS[code ushr 4])
                        append(HEX_DIGITS[code and 0x0F])
                    }
                }
            }
        }
    }
}

private fun String?.cleanUriCandidate(): String? {
    val raw = this ?: return null
    if (raw.hasUriControlCharacters()) return null
    val trimmed = raw.trim()
    if (trimmed.isEmpty() || trimmed.any { it.isWhitespace() }) return null
    return trimmed
}

private fun String.extractScheme(): String? {
    val separatorIndex = indexOf(':')
    if (separatorIndex <= 0) return null
    val candidate = substring(0, separatorIndex)
    if (!candidate.first().isLetter()) return null
    if (!candidate.all { it.isLetterOrDigit() || it == '+' || it == '-' || it == '.' }) return null
    return candidate
}

private fun String.extractAuthority(scheme: String?): String? {
    val afterScheme = when {
        scheme != null -> substring(scheme.length + 1)
        startsWith("//") -> this
        else -> return null
    }
    if (!afterScheme.startsWith("//")) return null
    return afterScheme
        .drop(2)
        .takeWhile { it != '/' && it != '?' && it != '#' }
        .substringAfterLast('@')
        .takeIf { it.isNotBlank() }
}

private fun String.extractPath(scheme: String?): String? {
    if (scheme == null) return null
    val afterScheme = substring(scheme.length + 1)
    if (!afterScheme.startsWith("//")) return null

    val authorityAndPath = afterScheme.drop(2)
    val slashIndex = authorityAndPath.indexOf('/')
    if (slashIndex < 0) return "/"

    return authorityAndPath.substring(slashIndex)
        .substringBefore('?')
        .substringBefore('#')
        .ifBlank { "/" }
}

private fun String.extractHost(): String? {
    if (hasInvalidPort()) return null
    val host = if (startsWith("[")) {
        substringAfter("[").substringBefore("]", missingDelimiterValue = "")
    } else {
        if (count { it == ':' } > 1) return null
        substringBefore(":")
    }
    return host.trim().takeIf { it.isNotBlank() }
}

private fun String.extractPort(): Int? {
    val portCandidate = explicitPortCandidate() ?: return null
    return portCandidate
        .takeIf { it.isNotEmpty() && it.all { char -> char.isDigit() } }
        ?.toIntOrNull()
        ?.takeIf { it >= 0 }
}

private fun String.hasInvalidPort(): Boolean {
    val portCandidate = explicitPortCandidate() ?: return false
    return portCandidate.isEmpty() ||
            !portCandidate.all { it.isDigit() } ||
            portCandidate.toIntOrNull() == null
}

private fun String.explicitPortCandidate(): String? {
    return if (startsWith("[")) {
        substringAfter("]", missingDelimiterValue = "")
            .takeIf { it.startsWith(":") }
            ?.drop(1)
    } else {
        takeIf { it.count { char -> char == ':' } == 1 }
            ?.substringAfter(":")
    }
}

private fun Int.isUrlFormSafeByte(): Boolean =
    this in 'A'.code..'Z'.code ||
            this in 'a'.code..'z'.code ||
            this in '0'.code..'9'.code ||
            this == '-'.code ||
            this == '_'.code ||
            this == '.'.code ||
            this == '*'.code

private const val SPACE_CODE = 0x20
private const val HEX_DIGITS = "0123456789ABCDEF"
