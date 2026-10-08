# ConnectTag Network Policy Library (KMP)

[![JitPack](https://jitpack.io/v/connecttag/network-policy-lib-kmp.svg)](https://jitpack.io/#connecttag/network-policy-lib-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-android%20%7C%20jvm%20%7C%20ios-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Production-grade cross-platform network security, URL validation, private network protection, sensitive header redaction, and DNS runtime policy governance for **Kotlin Multiplatform (KMP)** projects targeting Android, JVM, and iOS.

---

## 📦 Installation

Add JitPack repository to your root `settings.gradle.kts` (or `build.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `commonMain` dependencies in `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.connecttag:network-policy-lib-kmp:1.0.0")
        }
    }
}
```

---

## 🚀 Key Features

- **URL Validation & Sanitization**: `HttpUrlPolicy` guards against malformed URLs, control characters, and unsupported schemes.
- **Sensitive Header Redaction**: `HttpHeaderRedactionPolicy` identifies sensitive HTTP headers (tokens, cookies, HMAC signatures) before logging or caching.
- **Private Network Protection**: `PrivateNetworkUrlPolicy` validates RFC 1918 private IPv4 addresses, handles compact router IP inputs, and extracts gateways safely.
- **External URI Dispatching**: `ExternalUriPolicy` categorizes external URI intents (`geo:`, `tel:`, `sms:`, `mailto:`, `market:`) safely.
- **Pure KMP URI Parser**: `UriPolicy` and `UriComponentCodec` parse URLs and encode form query parameters across platforms without external dependencies.
- **Content Fetch Directives**: `ContentFetchPolicy` coordinates local cache versus remote fetching based on TTL, error cooldowns, and force refresh.
- **DNS Runtime Policies**: `DnsRuntimePolicy` models DoH (DNS-over-HTTPS) providers (Cloudflare, Google, Quad9) and racing timeouts.

---

## 📄 License

```text
Copyright 2026 Connect Tag

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
