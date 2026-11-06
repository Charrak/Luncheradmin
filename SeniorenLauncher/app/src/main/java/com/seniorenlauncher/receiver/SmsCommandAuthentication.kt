package com.seniorenlauncher.receiver

/** A single PIN at the beginning authenticates every command in a message. */
fun authenticateSmsCommand(body: String, pin: String, protectionEnabled: Boolean): String? {
    val text = body.trim()
    val match = Regex("^([0-9]{4})\\s+((?:#|LAUN_).*)$", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).matchEntire(text)
    if (match != null) {
        return if (pin.matches(Regex("[0-9]{4}")) && match.groupValues[1] == pin) match.groupValues[2] else null
    }
    if (protectionEnabled) return null
    return if (text.startsWith("#") || text.startsWith("LAUN_", ignoreCase = true)) text else null
}
