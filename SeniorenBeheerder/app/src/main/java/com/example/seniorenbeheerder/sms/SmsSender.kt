package com.example.seniorenbeheerder.sms

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

class SmsSender(private val context: Context) {
    private val smsManager: SmsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) context.getSystemService(SmsManager::class.java) else @Suppress("DEPRECATION") SmsManager.getDefault()

    fun sendSms(phoneNumber: String, message: String, language: String): Boolean {
        if (phoneNumber.isBlank()) {
            Toast.makeText(context, if (language == "nl") "Stel eerst een telefoonnummer in" else "Bitte zuerst eine Telefonnummer eingeben", Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            return true
        } catch (e: Exception) {
            Toast.makeText(context, if (language == "nl") "SMS kon niet worden verzonden" else "SMS konnte nicht gesendet werden", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}
