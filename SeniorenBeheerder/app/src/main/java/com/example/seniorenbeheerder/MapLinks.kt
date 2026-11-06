package com.example.seniorenbeheerder

/**
 * Baut die URIs, mit denen der empfangene Standort in einer Karten-App geöffnet wird.
 *
 * Reihenfolge der Versuche in der UI: geo:-URI (Google Maps direkt) -> per SMS
 * empfangener Link -> Google-Maps-Weblink. So funktioniert es auch ohne
 * installierte Google-Maps-App.
 */
object MapLinks {

    /** geo:-URI; das Schema ist der Standardweg zu installierten Karten-Apps. */
    fun geoUri(latitude: Double, longitude: Double): String =
        "geo:$latitude,$longitude?q=$latitude,$longitude"

    /** Weblink, der im Browser oder in der Maps-App landet. */
    fun webUri(latitude: Double, longitude: Double): String =
        "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"

    /**
     * Liefert den empfangenen Link, wenn er als http(s)-URL brauchbar ist.
     * Der Launcher schickt z. B. "📍 Locatie: https://www.google.com/maps/...".
     */
    fun receivedUri(received: String?): String? {
        val trimmed = received?.trim().orEmpty()
        if (!(trimmed.startsWith("http://") || trimmed.startsWith("https://"))) return null
        if (trimmed.any { it.isWhitespace() }) return null
        return trimmed
    }
}
