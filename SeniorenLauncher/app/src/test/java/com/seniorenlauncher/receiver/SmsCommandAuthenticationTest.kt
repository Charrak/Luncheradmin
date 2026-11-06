package com.seniorenlauncher.receiver

import org.junit.Assert.*
import org.junit.Test

class SmsCommandAuthenticationTest {
    @Test fun pinProtectionRejectsMissingAndWrongPin() {
        assertEquals("#WAAR", authenticateSmsCommand("1234 #WAAR", "1234", true))
        assertEquals("LAUN_ZOEK", authenticateSmsCommand("1234 LAUN_ZOEK", "1234", true))
        assertEquals("#STATUS #WAAR", authenticateSmsCommand("1234 #STATUS #WAAR", "1234", true))
        assertNull(authenticateSmsCommand("#WAAR", "1234", true))
        assertNull(authenticateSmsCommand("LAUN_ZOEK", "1234", true))
        assertNull(authenticateSmsCommand("4321 #WAAR", "1234", true))
        assertNull(authenticateSmsCommand("1234#WAAR", "1234", true))
        assertNull(authenticateSmsCommand("1234 #WAAR", "12", true))
    }
    @Test fun unprotectedModeRetainsCompatibility() {
        assertEquals("#WAAR", authenticateSmsCommand("#WAAR", "1234", false))
        assertEquals("LAUN_ZOEK", authenticateSmsCommand("LAUN_ZOEK", "1234", false))
        assertEquals("#WAAR", authenticateSmsCommand("1234 #WAAR", "1234", false))
        assertNull(authenticateSmsCommand("4321 #WAAR", "1234", false))
    }
}
