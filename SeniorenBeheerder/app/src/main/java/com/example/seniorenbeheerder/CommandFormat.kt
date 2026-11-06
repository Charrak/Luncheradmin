package com.example.seniorenbeheerder

/** Returns null rather than sending an unprotected command if the saved PIN is invalid. */
fun isValidPin(pin: String): Boolean = pin.matches(Regex("[0-9]{4}"))

fun formatCommand(command: String, pin: String, enabled: Boolean): String? =
    if (!enabled) command else if (isValidPin(pin)) "$pin $command" else null
