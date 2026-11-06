package com.example.seniorenbeheerder

import org.junit.Assert.*
import org.junit.Test

class CommandFormatTest {
    @Test fun protectedAndUnprotectedCommands() {
        assertEquals("1234 #WAAR", formatCommand("#WAAR", "1234", true))
        assertEquals("1234 LAUN_ZOEK", formatCommand("LAUN_ZOEK", "1234", true))
        assertEquals("#STATUS", formatCommand("#STATUS", "1234", false))
        assertNull(formatCommand("#WAAR", "12x4", true))
        assertNull(formatCommand("#WAAR", "", true))
        assertTrue(isValidPin("0000"))
        assertFalse(isValidPin("１２３４"))
    }
}
