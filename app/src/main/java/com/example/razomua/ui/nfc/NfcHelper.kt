package com.example.razomua.ui.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Parcelable

class NfcHelper(private val activity: Activity) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)

    /** Створення NDEF-повідомлення */
    fun createNdefMessage(text: String): NdefMessage {
        val record = NdefRecord.createTextRecord("en", text)
        return NdefMessage(arrayOf(record))
    }

    /** Запис у NFC-мітку */
    fun writeToTag(message: NdefMessage, tag: Tag): Boolean {
        val ndef = Ndef.get(tag)
        return if (ndef != null) {
            ndef.connect()
            if (!ndef.isWritable) {
                ndef.close(); return false
            }
            ndef.writeNdefMessage(message)
            ndef.close()
            true
        } else {
            val formatable = NdefFormatable.get(tag) ?: return false
            formatable.connect()
            formatable.format(message)
            formatable.close()
            true
        }
    }

    /** Зчитування профілю */
    fun readProfileId(intent: Intent): String? {
        val rawMsgs =
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    as? Array<Parcelable> ?: return null

        val message = rawMsgs[0] as NdefMessage
        val record = message.records[0]
        return String(record.payload)
    }
}
