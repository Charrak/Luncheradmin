package com.example.seniorenbeheerder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapLinksTest {

    @Test
    fun geoUriContainsCoordinatesTwice() {
        assertEquals(
            "geo:51.0441737,3.7436598?q=51.0441737,3.7436598",
            MapLinks.geoUri(51.0441737, 3.7436598)
        )
    }

    @Test
    fun geoUriHandlesNegativeCoordinates() {
        assertEquals("geo:-33.9,-70.6?q=-33.9,-70.6", MapLinks.geoUri(-33.9, -70.6))
    }

    @Test
    fun webUriUsesMapsSearchApi() {
        assertEquals(
            "https://www.google.com/maps/search/?api=1&query=51.5,4.9",
            MapLinks.webUri(51.5, 4.9)
        )
    }

    @Test
    fun receivedUriAcceptsLocationLink() {
        val sms = "📍 Locatie: https://www.google.com/maps/search/?api=1&query=51.044,3.743"
        assertEquals(
            "https://www.google.com/maps/search/?api=1&query=51.044,3.743",
            MapLinks.receivedUri(sms)
        )
    }

    @Test
    fun receivedUriRejectsOtherSmsContent() {
        assertNull(MapLinks.receivedUri(null))
        assertNull(MapLinks.receivedUri(""))
        assertNull(MapLinks.receivedUri("Locatie: "))
        assertNull(MapLinks.receivedUri("Batterij: 85%"))
        assertNull(MapLinks.receivedUri("geen link hier"))
    }

    @Test
    fun receivedUriRejectsTextWithSpaces() {
        assertNull(MapLinks.receivedUri("https://example.com/a b"))
    }
}
