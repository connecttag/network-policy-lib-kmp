package org.connecttag.lib.networkpolicy

enum class ContentFetchDirective {
    FETCH_REMOTE,
    USE_CACHE,
    STOP,
}

data class ContentFetchContext(
    val forceRefresh: Boolean,
    val resourceEnabled: Boolean,
    val cacheOnly: Boolean,
    val cachedValueAvailable: Boolean,
    val lastFetchedAtMillis: Long,
    val nextRetryAtMillis: Long,
    val minIntervalMillis: Long?,
)

fun ContentFetchContext.shouldFetch(nowMillis: Long): ContentFetchDirective {
    if (!resourceEnabled || cacheOnly) {
        return if (cachedValueAvailable) ContentFetchDirective.USE_CACHE else ContentFetchDirective.STOP
    }
    if (forceRefresh) return ContentFetchDirective.FETCH_REMOTE
    if (nextRetryAtMillis > nowMillis) {
        return if (cachedValueAvailable) ContentFetchDirective.USE_CACHE else ContentFetchDirective.STOP
    }
    val interval = minIntervalMillis?.takeIf { it > 0L }
    if (
        cachedValueAvailable &&
        interval != null &&
        lastFetchedAtMillis > 0L &&
        nowMillis - lastFetchedAtMillis < interval
    ) {
        return ContentFetchDirective.USE_CACHE
    }
    return ContentFetchDirective.FETCH_REMOTE
}
