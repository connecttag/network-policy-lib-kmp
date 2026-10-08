package org.connecttag.lib.networkpolicy

object PrivateNetworkUrlPolicy {

    fun normalizeHttpUrl(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""
        val url = if (HttpUrlPolicy.isHttpUrl(trimmed)) trimmed else "http://$trimmed"
        return normalizeCompactPrivateIpv4Url(url)
    }

    fun buildUrl(baseUrl: String, path: String): String {
        if (baseUrl.isBlank()) return ""

        val normalizedBaseUrl = normalizeHttpUrl(baseUrl)
        val trimmedPath = path.trim()
        if (trimmedPath.isBlank()) return normalizedBaseUrl
        if (HttpUrlPolicy.isHttpUrl(trimmedPath)) return normalizeHttpUrl(trimmedPath)
        if (containsRoute(normalizedBaseUrl, trimmedPath)) return normalizedBaseUrl

        return when {
            normalizedBaseUrl.endsWith("/") && trimmedPath.startsWith("/") ->
                normalizedBaseUrl.dropLast(1) + trimmedPath

            !normalizedBaseUrl.endsWith("/") && !trimmedPath.startsWith("/") ->
                "$normalizedBaseUrl/$trimmedPath"

            else -> normalizedBaseUrl + trimmedPath
        }
    }

    fun extractHost(value: String): String? {
        val normalized = normalizeHttpUrl(value)
        val authority = normalized
            .substringAfter("://", missingDelimiterValue = normalized)
            .takeWhile { it != '/' && it != '?' && it != '#' }
            .substringAfterLast('@')

        val host = if (authority.startsWith("[")) {
            authority.substringAfter("[").substringBefore("]")
        } else {
            authority.substringBefore(":")
        }

        return host.trim().takeIf { it.isNotBlank() }
    }

    fun hostsMatch(firstUrl: String?, secondUrl: String?): Boolean {
        val firstHost = firstUrl?.let { extractHost(it) } ?: return false
        val secondHost = secondUrl?.let { extractHost(it) } ?: return false
        return firstHost.equals(secondHost, ignoreCase = true)
    }

    fun isSupportedPrivateIpv4Url(value: String): Boolean {
        val host = extractHost(value) ?: return false
        val parts = host.split(".").map { it.toIntOrNull() }
        if (parts.size != IPV4_PART_COUNT || parts.any { it == null }) return false
        val octets = parts.filterNotNull()
        if (octets.any { it !in IPV4_OCTET_RANGE }) return false

        val first = octets[0]
        val second = octets[1]
        return first == 10 ||
                first == 192 && second == 168 ||
                first == 172 && second in 16..31
    }

    fun findSupportedPrivateGatewayAddress(
        defaultRouteGateways: List<String?>,
        routeGateways: List<String?>,
    ): String? {
        val defaultGateway = defaultRouteGateways.firstOrNull { gateway ->
            gateway != null && isSupportedPrivateIpv4Url(gateway)
        }
        if (defaultGateway != null) return defaultGateway

        return routeGateways.firstOrNull { gateway ->
            gateway != null && isSupportedPrivateIpv4Url(gateway)
        }
    }

    private fun normalizeCompactPrivateIpv4Url(value: String): String {
        val match = Regex(
            pattern = "^(https?://)192168\\.(\\d{1,3})\\.(\\d{1,3})(.*)$",
            option = RegexOption.IGNORE_CASE,
        ).matchEntire(value) ?: return value

        val third = match.groupValues[2].toIntOrNull() ?: return value
        val fourth = match.groupValues[3].toIntOrNull() ?: return value
        if (third !in IPV4_OCTET_RANGE || fourth !in IPV4_OCTET_RANGE) return value

        return "${match.groupValues[1]}192.168.$third.$fourth${match.groupValues[4]}"
    }

    private fun containsRoute(baseUrl: String, path: String): Boolean {
        val baseRoute = routeKey(baseUrl)
        val pathRoute = routeKey(path)
        if (baseRoute.isBlank() || pathRoute.isBlank()) return false

        return baseRoute.equals(pathRoute, ignoreCase = true) ||
                baseRoute.startsWith("$pathRoute?", ignoreCase = true) ||
                baseRoute.startsWith("$pathRoute#", ignoreCase = true) ||
                baseRoute.endsWith("/$pathRoute", ignoreCase = true) ||
                baseRoute.contains("/$pathRoute?", ignoreCase = true) ||
                baseRoute.contains("/$pathRoute#", ignoreCase = true)
    }

    private fun routeKey(value: String): String {
        val trimmed = value.trim()
        val schemeIndex = trimmed.indexOf("://")
        val route = if (schemeIndex >= 0) {
            val afterAuthority = trimmed.substring(schemeIndex + 3)
            val routeStart = afterAuthority.indexOfFirst { it == '/' || it == '?' || it == '#' }
            if (routeStart < 0) "" else afterAuthority.substring(routeStart)
        } else {
            trimmed
        }

        return route.trim().trim('/').takeIf { it.isNotBlank() } ?: ""
    }

    private val IPV4_OCTET_RANGE = 0..255
    private const val IPV4_PART_COUNT = 4
}
