package com.example.seniorenbeheerder.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val body = message.displayMessageBody
                
                Log.d("SmsReceiver", "Ontvangen van $sender: $body")
                
                // Hier sturen we een broadcast naar de app om de UI te updaten
                val updateIntent = Intent("com.example.seniorenbeheerder.SMS_UPDATED")
                updateIntent.putExtra("sender", sender)
                updateIntent.putExtra("body", body)
                context.sendBroadcast(updateIntent)
            }
        }
    }
}
