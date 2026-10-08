package org.connecttag.lib.networkpolicy

/**
 * Portable DNS intent retained for runtime configuration and future engines.
 * Ktor engines own actual name resolution; the HTTP layer does not expose an OkHttp DNS adapter.
 */
data class DnsRuntimePolicy(
    val dnsMode: DnsMode = DnsMode.AUTO,
    val dohProviders: List<DohProviderDnsPolicy> = defaultDohProviderDnsPolicies(),
    val dnsRacingEnabled: Boolean = true,
    val dnsRaceTimeoutMillis: Long = 2_500L,
    val dnsProviderCooldownSeconds: Int = 300,
    val dnsBootstrapConnectTimeoutSeconds: Int = 15,
    val safeModeHosts: Map<String, List<String>> = emptyMap(),
)

data class DohProviderDnsPolicy(
    val id: String,
    val url: String,
    val bootstrapHosts: List<String> = emptyList(),
    val includeIpv6: Boolean = true,
    val post: Boolean = true,
    val orderIndex: Int = 0,
)

enum class DnsMode {
    AUTO,
    SYSTEM,
    DOH_ONLY,
    ;

    companion object {
        fun from(value: String?): DnsMode {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: AUTO
        }
    }
}

fun interface DnsPolicyProvider {
    fun currentPolicy(): DnsRuntimePolicy
}

fun defaultDohProviderDnsPolicies(): List<DohProviderDnsPolicy> = listOf(
    DohProviderDnsPolicy(
        id = "cloudflare",
        url = DEFAULT_CLOUDFLARE_DOH_URL,
        bootstrapHosts = listOf("162.159.46.1", "1.1.1.1", "1.0.0.1"),
        orderIndex = 0,
    ),
    DohProviderDnsPolicy(
        id = "google",
        url = "https://dns.google/dns-query",
        bootstrapHosts = listOf("8.8.8.8", "8.8.4.4"),
        orderIndex = 1,
    ),
    DohProviderDnsPolicy(
        id = "quad9",
        url = "https://dns.quad9.net/dns-query",
        bootstrapHosts = listOf("9.9.9.9", "149.112.112.112"),
        orderIndex = 2,
    ),
)

const val DEFAULT_CLOUDFLARE_DOH_URL: String = "https://cloudflare-dns.com/dns-query"
