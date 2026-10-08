package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertEquals

class ContentFetchPolicyTest {
    @Test
    fun recentCacheAvoidsRemoteFetch() {
        val decision = ContentFetchContext(
            forceRefresh = false,
            resourceEnabled = true,
            cacheOnly = false,
            cachedValueAvailable = true,
            lastFetchedAtMillis = 900L,
            nextRetryAtMillis = 0L,
            minIntervalMillis = 200L,
        ).shouldFetch(nowMillis = 1_000L)

        assertEquals(ContentFetchDirective.USE_CACHE, decision)
    }

    @Test
    fun forcedRefreshBypassesIntervalAndRetryCooldown() {
        val decision = ContentFetchContext(
            forceRefresh = true,
            resourceEnabled = true,
            cacheOnly = false,
            cachedValueAvailable = true,
            lastFetchedAtMillis = 999L,
            nextRetryAtMillis = 9_999L,
            minIntervalMillis = 10_000L,
        ).shouldFetch(nowMillis = 1_000L)

        assertEquals(ContentFetchDirective.FETCH_REMOTE, decision)
    }
}
