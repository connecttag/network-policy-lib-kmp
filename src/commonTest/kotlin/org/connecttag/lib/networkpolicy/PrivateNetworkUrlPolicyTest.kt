package org.connecttag.lib.networkpolicy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrivateNetworkUrlPolicyTest {

    @Test
    fun normalizeHttpUrlAddsSchemeAndCorrectsCompactPrivateHost() {
        assertEquals("http://192.168.8.1", PrivateNetworkUrlPolicy.normalizeHttpUrl("192.168.8.1"))
        assertEquals("http://192.168.4.1", PrivateNetworkUrlPolicy.normalizeHttpUrl("192168.4.1"))
    }

    @Test
    fun buildUrlDoesNotDuplicateExistingRoute() {
        assertEquals(
            "http://192.168.0.1/login.asp?lang=ar",
            PrivateNetworkUrlPolicy.buildUrl("http://192.168.0.1/login.asp?lang=ar", "/login.asp"),
        )
        assertEquals(
            "http://192.168.8.1/#/Login",
            PrivateNetworkUrlPolicy.buildUrl("http://192.168.8.1/#/Login", "/#/Login"),
        )
    }

    @Test
    fun extractHostReadsHostFromUrlWithPort() {
        assertEquals("192.168.1.1", PrivateNetworkUrlPolicy.extractHost("http://192.168.1.1:8080/index.html"))
    }

    @Test
    fun hostsMatchComparesNormalizedHosts() {
        assertTrue(
            PrivateNetworkUrlPolicy.hostsMatch(
                "http://192.168.8.1/html/login.html",
                "192168.8.1",
            ),
        )
        assertFalse(PrivateNetworkUrlPolicy.hostsMatch("192.168.8.1", "192.168.1.1"))
    }

    @Test
    fun privateIpv4PolicyAcceptsOnlyPrivateIpv4Ranges() {
        assertTrue(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("10.0.0.1"))
        assertTrue(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("172.31.255.254"))
        assertTrue(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("http://192168.4.1"))
        assertFalse(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("8.8.8.8"))
        assertFalse(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("172.32.0.1"))
        assertFalse(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("192.168.1.999"))
        assertFalse(PrivateNetworkUrlPolicy.isSupportedPrivateIpv4Url("modem.local"))
    }

    @Test
    fun gatewaySelectionPrefersPrivateDefaultGateway() {
        assertEquals(
            "192.168.8.1",
            PrivateNetworkUrlPolicy.findSupportedPrivateGatewayAddress(
                defaultRouteGateways = listOf("8.8.8.8", "192.168.8.1"),
                routeGateways = listOf("10.0.0.1"),
            ),
        )
        assertNull(
            PrivateNetworkUrlPolicy.findSupportedPrivateGatewayAddress(
                defaultRouteGateways = listOf(null, "8.8.8.8"),
                routeGateways = listOf("172.32.0.1", "modem.local"),
            ),
        )
    }
}
