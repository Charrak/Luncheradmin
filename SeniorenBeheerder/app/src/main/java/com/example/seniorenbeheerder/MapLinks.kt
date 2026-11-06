package com.example.seniorenbeheerder

/**
 * Baut die URIs, mit denen der empfangene Standort in einer Karten-App geöffnet wird.
 *
 * Reihenfolge der Versuche in der UI: geo:-URI (Google Maps direkt) -> per SMS
 * empfangener Link -> Google-Maps-Weblink. So funktioniert es auch ohne
 * installierte Google-Maps-App.
 */
object MapLinks {

    /** Erkennt eine http(s)-URL auch innerhalb einer längeren SMS. */
    private val URL_PATTERN = Regex("https?://\\S+")

    /** Zeichen, die in SMS-Text häufig direkt an die URL anschließen. */
    private const val TRAILING_PUNCTUATION = ".,;:!?)\"'»>"

    /** geo:-URI; das Schema ist der Standardweg zu installierten Karten-Apps. */
    fun geoUri(latitude: Double, longitude: Double): String =
        "geo:$latitude,$longitude?q=$latitude,$longitude"

    /** Weblink, der im Browser oder in der Maps-App landet. */
    fun webUri(latitude: Double, longitude: Double): String =
        "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"

    /**
     * Liefert den empfangenen Link, wenn die SMS eine http(s)-URL enthält.
     * Der Launcher schickt z. B. "📍 Locatie: https://www.google.com/maps/...".
     */
    fun receivedUri(received: String?): String? {
        val text = received?.trim().orEmpty()
        if (text.isEmpty()) return null
        return URL_PATTERN.find(text)?.value?.trimEnd(*TRAILING_PUNCTUATION.toCharArray())
    }
}
